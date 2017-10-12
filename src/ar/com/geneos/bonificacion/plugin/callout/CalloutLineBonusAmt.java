package ar.com.geneos.bonificacion.plugin.callout;

import java.math.BigDecimal;
import java.util.Properties;

import org.openXpertya.model.MField;
import org.openXpertya.model.MTab;
import org.openXpertya.model.MTax;
import org.openXpertya.plugin.CalloutPluginEngine;
import org.openXpertya.plugin.MPluginStatusCallout;
import org.openXpertya.util.Env;

public class CalloutLineBonusAmt extends CalloutPluginEngine {

	public CalloutLineBonusAmt()  {
		// TODO Auto-generated constructor stub
	}
	
	public MPluginStatusCallout lineBonusAmt(Properties ctx, int WindowNo, MTab mTab, MField mField, Object value) {
		if (isCalloutActive() || (value == null)) {
			return state;
		}
		
		Float lineBonus = ((BigDecimal)value).floatValue();
		if(lineBonus != null) {
			if(lineBonus > 0) {
				// Actualizo el neto de linea con el descuento
				BigDecimal lineNetAmt  = ((BigDecimal)mTab.getValue("QtyInvoiced")).multiply((BigDecimal)mTab.getValue("PriceActual"));
				Float percent = (100F - lineBonus)/100;
				lineNetAmt = lineNetAmt.multiply(new BigDecimal(percent));
				mTab.setValue("LineNetAmt", lineNetAmt);
				mTab.setValue("LineNetAmount", lineNetAmt);
				
				// Actualizo el impuesto sobre el neto antes calculado
				BigDecimal TaxAmt = Env.ZERO;
		        if(mField.getColumnName().equals("TaxAmt")) {
		            TaxAmt = (BigDecimal)mTab.getValue("TaxAmt");
		        } else {
		            Integer taxID = (Integer)mTab.getValue("C_Tax_ID");
		            if(taxID != null) {
		                MTax tax = new MTax(ctx, taxID.intValue(), null);		             
		                percent = (tax.getRate().floatValue() / 100);		              
		                TaxAmt = lineNetAmt.multiply(new BigDecimal(percent));
		                mTab.setValue("TaxAmt",TaxAmt);
		            }
		        }
			} else {
				if(lineBonus < 0) {
					state.setContinueStatus(MPluginStatusCallout.STATE_FALSE);
					state.setErrorMessage("Bonificacion negativa. Debe ser mayor o igual a 0");
				}
			}
		}
		
		setCalloutActive(false);
		
		return state;
	}

}
