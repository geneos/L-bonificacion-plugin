package ar.com.geneos.bonificacion.plugin.callout;

import java.math.BigDecimal;
import java.util.Properties;

import org.openXpertya.model.MField;
import org.openXpertya.model.MTab;
import org.openXpertya.plugin.CalloutPluginEngine;
import org.openXpertya.plugin.MPluginStatusCallout;

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
				BigDecimal lineNetAmt  = ((BigDecimal)mTab.getValue("QtyInvoiced")).multiply((BigDecimal)mTab.getValue("PriceActual"));
				Float percent = (100F - lineBonus)/100;
				mTab.setValue("LineNetAmt", lineNetAmt.multiply(new BigDecimal(percent)));
				mTab.setValue("LineNetAmount", lineNetAmt.multiply(new BigDecimal(percent)));
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
