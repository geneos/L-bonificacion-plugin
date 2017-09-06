package ar.com.geneos.bonificacion.plugin.model;

import java.math.BigDecimal;
import java.util.Properties;

import org.openXpertya.model.PO;
import org.openXpertya.plugin.MPluginPO;
import org.openXpertya.plugin.MPluginStatusPO;

public class MInvoiceLine extends MPluginPO {

	public MInvoiceLine(PO po, Properties ctx, String trxName, String aPackage) {
		super(po, ctx, trxName, aPackage);
		// TODO Auto-generated constructor stub
	}
	
	public MPluginStatusPO postBeforeSave(PO po, boolean newRecord) {
		org.openXpertya.model.MInvoiceLine invoiceLine = (org.openXpertya.model.MInvoiceLine)po;
		Float lineNetAmt = ((BigDecimal)invoiceLine.getLineNetAmt()).floatValue();
		Float lineBonusAmt = ((BigDecimal)invoiceLine.getLineBonusAmt()).floatValue();
		if(lineBonusAmt != null) {
			if(lineBonusAmt > 0) {
				Float percent = (100F - lineBonusAmt)/100;
				lineNetAmt = lineNetAmt * percent;
			} else {
				if(lineBonusAmt < 0) {
					status_po.setContinueStatus(MPluginStatusPO.STATE_FALSE);
					status_po.setErrorMessage("Bonificacion negativa. Debe ser mayor o igual a 0");
				}
			}
		}
		Float lineTaxImport = ((BigDecimal)invoiceLine.getTaxAmt()).floatValue();
		invoiceLine.setLineNetAmt(new BigDecimal(lineNetAmt));
		invoiceLine.setLineTotalAmt(new BigDecimal(lineNetAmt + lineTaxImport));
		invoiceLine.setLineNetAmount(new BigDecimal(lineNetAmt));
		
		return status_po;
	} 

}
