/**
 * Copyright (C) 2012-2013 Alexander Pinnow
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Library General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Library General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Library General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 **/
package net.sourceforge.fixagora.sap.server.control.component;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sourceforge.fixagora.basis.server.control.BasisPersistenceHandler;
import net.sourceforge.fixagora.basis.server.control.BusinessComponentHandler;
import net.sourceforge.fixagora.basis.shared.model.communication.LogEntryResponse;
import net.sourceforge.fixagora.basis.shared.model.persistence.CounterPartyPartyID;
import net.sourceforge.fixagora.basis.shared.model.persistence.Counterparty;
import net.sourceforge.fixagora.basis.shared.model.persistence.FSecurity;
import net.sourceforge.fixagora.basis.shared.model.persistence.FUser;
import net.sourceforge.fixagora.basis.shared.model.persistence.FUserPartyID;
import net.sourceforge.fixagora.basis.shared.model.persistence.LogEntry;
import net.sourceforge.fixagora.basis.shared.model.persistence.LogEntry.Level;
import net.sourceforge.fixagora.basis.shared.model.persistence.SecurityAltIDGroup;
import net.sourceforge.fixagora.basis.shared.model.persistence.Trader;
import net.sourceforge.fixagora.basis.shared.model.persistence.TraderPartyID;
import net.sourceforge.fixagora.sap.shared.communication.SAPTradeCaptureEntry;
import net.sourceforge.fixagora.sap.shared.communication.SAPTradeCaptureEntry.ExportStatus;
import net.sourceforge.fixagora.sap.shared.persistence.SAPTradeCapture;

import org.jboss.netty.channel.Channel;

import com.sap.mw.jco.IFunctionTemplate;
import com.sap.mw.jco.JCO;
import com.sap.mw.jco.JCO.Field;
import com.sap.mw.jco.JCO.Function;
import com.sap.mw.jco.JCO.Structure;

/**
 * The Class SAPFIXAgoraConnector.
 */
public class SAPFIXAgoraConnector {

	private JCO.Client mConnection;
	private IFunctionTemplate functionTemplate;
	private IFunctionTemplate functionTemplate2;
	private BasisPersistenceHandler basisPersistenceHandler;
	private BusinessComponentHandler businessComponentHandler;
	private SAPTradeCapture sapTradeCapture;

	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
	private DecimalFormat decimalFormat2 = new DecimalFormat("#,##0.########");


	/**
	 * Instantiates a new sAPFIX agora connector.
	 *
	 * @param sapTradeCapture the sap trade capture
	 * @param basisPersistenceHandler the basis persistence handler
	 * @param businessComponentHandler the business component handler
	 */
	public SAPFIXAgoraConnector(SAPTradeCapture sapTradeCapture, BasisPersistenceHandler basisPersistenceHandler,
			BusinessComponentHandler businessComponentHandler) {

		super();
		mConnection = JCO.createClient(sapTradeCapture.getSapClient(), sapTradeCapture.getSapUserName(), sapTradeCapture.getSapPassword(), "EN",
				sapTradeCapture.getSapServerName(), sapTradeCapture.getSapSystemNumber());
		this.basisPersistenceHandler = basisPersistenceHandler;
		this.businessComponentHandler = businessComponentHandler;
		this.sapTradeCapture = sapTradeCapture;
	}

	/**
	 * Connect.
	 */
	public void connect() {

		mConnection.connect();
		JCO.Repository mRepository = new JCO.Repository("Trade Management", mConnection);
		functionTemplate = mRepository.getFunctionTemplate("BAPI_FTR_SECURITY_CREATE");
		functionTemplate2 = mRepository.getFunctionTemplate("BAPI_TRANSACTION_COMMIT");
	}

	/**
	 * Disconnect.
	 */
	public void disconnect() {

		mConnection.disconnect();
	}

	/**
	 * Export sap trade capture entry.
	 *
	 * @param sapTradeCaptureEntry the sap trade capture entry
	 * @param channel the channel
	 * @param test the test
	 */
	public void exportSAPTradeCaptureEntry(SAPTradeCaptureEntry sapTradeCaptureEntry, Channel channel, boolean test) {
		
		FSecurity security = basisPersistenceHandler.findById(FSecurity.class, sapTradeCaptureEntry.getSecurity(), businessComponentHandler);

		Counterparty counterparty = basisPersistenceHandler.findById(Counterparty.class, sapTradeCaptureEntry.getCounterparty(), businessComponentHandler);

		if (security == null || counterparty == null) {
			if (test)
				sapTradeCaptureEntry.setExportStatus(ExportStatus.TEST_FAILED);
			else
				sapTradeCaptureEntry.setExportStatus(ExportStatus.FAILED);
			return;
		}
		
		StringBuffer stringBuffer = new StringBuffer("Start ");

		if(test)
			stringBuffer.append("test ");
		stringBuffer.append("SAP export ");
		stringBuffer.append(" for trade  ");

		Trader trader = null;

		if (sapTradeCaptureEntry.getTrader() != null) {
			trader = basisPersistenceHandler.findById(Trader.class, sapTradeCaptureEntry.getTrader(), businessComponentHandler);
		}

		FUser user = null;

		if (sapTradeCaptureEntry.getUser() != null) {
			user = basisPersistenceHandler.findById(FUser.class, sapTradeCaptureEntry.getUser(), businessComponentHandler);
		}

		Function function = functionTemplate.getFunction();
		Structure securityStructure = function.getImportParameterList().getStructure("SECURITY");

		String securityIDSource = sapTradeCapture.getSecurityIDSource();
		securityStructure.setValue(security.getSecurityID(), "SECURITY_ID");
		if (securityIDSource != null)
			for (SecurityAltIDGroup securityAltIDGroup : security.getSecurityDetails().getSecurityAltIDGroups())
				if (securityIDSource.equals(securityAltIDGroup.getSecurityAltIDSource()))
					securityStructure.setValue(securityAltIDGroup.getSecurityAltID(), "SECURITY_ID");

		securityStructure.setValue(sapTradeCapture.getSapSecuritiesAccount(), "SECURITY_ACCOUNT");

		if (sapTradeCaptureEntry.getSettlementDate() != null) {
			securityStructure.setValue(simpleDateFormat.format(new Date(sapTradeCaptureEntry.getSettlementDate())), "POSITION_VALUE_DATE");
			securityStructure.setValue(simpleDateFormat.format(new Date(sapTradeCaptureEntry.getSettlementDate())), "CALCULATE_DATE");
			securityStructure.setValue(simpleDateFormat.format(new Date(sapTradeCaptureEntry.getSettlementDate())), "PAYMENT_DATE");
		}

		
			if (security != null && security.getSecurityDetails() != null && security.getSecurityDetails().getContractMultiplier() == null
					&& sapTradeCaptureEntry.getLastQuantity() != null)
				securityStructure.setValue(sapTradeCaptureEntry.getLastQuantity(), "NUMBER_UNITS");
		
			if (security != null && security.getSecurityDetails() != null && security.getSecurityDetails().getContractMultiplier() != null
					&& sapTradeCaptureEntry.getLastQuantity() != null)
				securityStructure.setValue(sapTradeCaptureEntry.getLastQuantity(), "NOMINAL_AMOUNT");
			
			if (security != null && security.getSecurityDetails() != null && security.getSecurityDetails().getCurrency() != null
					&& sapTradeCaptureEntry.getLastQuantity() != null)
				securityStructure.setValue(security.getSecurityDetails().getCurrency(), "PRICE_UNIT");
	
		securityStructure.setValue(sapTradeCaptureEntry.getLastPrice(), "PRICE");

		securityStructure.setValue(security.getSecurityDetails().getCurrency(), "PAYMENT_CURRENCY");
		
		if(sapTradeCapture.isSetExchange())
			securityStructure.setValue(sapTradeCaptureEntry.getMarket(), "EXCHANGE");

		Structure generalContractDataStructure = function.getImportParameterList().getStructure("GENERALCONTRACTDATA");
		generalContractDataStructure.setValue(sapTradeCapture.getSapCompanyCode(), "COMPANY_CODE");
		
		String side = " from ";
		
		if (sapTradeCaptureEntry.getSide() == SAPTradeCaptureEntry.Side.SELL)
		{
			stringBuffer.append("sell ");
			side = " to ";
			generalContractDataStructure.setValue(sapTradeCapture.getSapTransactionTypeSell(), "TRANSACTION_TYPE");
		}
		else
		{
			stringBuffer.append("buy ");
			generalContractDataStructure.setValue(sapTradeCapture.getSapTransactionTypeBuy(), "TRANSACTION_TYPE");
		}

		if (trader != null) {
			
			String partyID = null;
			
			for (TraderPartyID traderPartyID : trader.getTraderPartyIDs()) {
				if (traderPartyID.getAbstractBusinessComponent()==null)
				{
					if(partyID==null)
						partyID = traderPartyID.getPartyID();
				}
				else if (traderPartyID.getPartyRole() != null && traderPartyID.getPartyRole() == 37
						&& traderPartyID.getAbstractBusinessComponent().getId() == sapTradeCapture.getId())
					partyID = traderPartyID.getPartyID();
			}
			
			if(partyID!=null)
				generalContractDataStructure.setValue(partyID, "CONTACT_PERSON");
		}
		
		String partner = null;
		
		for (CounterPartyPartyID counterPartyPartyID : counterparty.getCounterPartyPartyIDs()) {
			if(counterPartyPartyID.getAbstractBusinessComponent()==null)
			{
				if(partner==null)
					partner = counterPartyPartyID.getPartyID();
			}
			else if (counterPartyPartyID.getPartyRole() != null && counterPartyPartyID.getPartyRole() == 17
					&& counterPartyPartyID.getAbstractBusinessComponent().getId() == sapTradeCapture.getId())
				partner = counterPartyPartyID.getPartyID();
		}
		
		if(partner!=null)
			generalContractDataStructure.setValue(partner, "PARTNER");
		
		generalContractDataStructure.setValue(sapTradeCapture.getSapGeneralValuationClass(), "VALUATION_CLASS");
		generalContractDataStructure.setValue(sapTradeCaptureEntry.getCounterpartyOrderId(), "EXTERNAL_REFERENCE");
		generalContractDataStructure.setValue(sapTradeCaptureEntry.getTradeId(), "INTERNAL_REFERENCE");

		
		if (sapTradeCapture.isSetTrader()&&user!=null)
		{
			
			String userID = null;
			
			for (FUserPartyID userPartyID : user.getfUserPartyIDs()) {
				if(userPartyID.getAbstractBusinessComponent()==null)
				{
					if(userID==null)
						userID = userPartyID.getPartyID();
				}
				else if (userPartyID.getPartyRole() != null && userPartyID.getPartyRole() == 12
						&& userPartyID.getAbstractBusinessComponent().getId() == sapTradeCapture.getId())
					userID = userPartyID.getPartyID();
			}
			
			if(userID!=null)
				generalContractDataStructure.setValue(userID, "TRADER");
			
		}
		
		if(test)
		{
			Field testRunField = function.getImportParameterList().getField("TESTRUN");
			testRunField.setValue("X");
		}
		else
		{
			
		}
		
		stringBuffer.append(decimalFormat2.format(sapTradeCaptureEntry.getLastQuantity()));

		stringBuffer.append(" ");

		stringBuffer.append(security.getName());

		stringBuffer.append(side);
		stringBuffer.append(counterparty.getName());
		if (trader != null) {
			stringBuffer.append(" (");
			stringBuffer.append(trader.getName());
			stringBuffer.append(")");
		}

		if (sapTradeCaptureEntry.getSettlementDate() != null) {
			stringBuffer.append(" settlement ");
			stringBuffer.append(simpleDateFormat.format(new Date(sapTradeCaptureEntry.getSettlementDate())));
		}
		
		LogEntry logEntry2 = new LogEntry();

		logEntry2.setLogLevel(Level.INFO);
		logEntry2.setLogDate(new Date());
		logEntry2.setMessageComponent(sapTradeCapture.getName());
		logEntry2.setMessageText(stringBuffer.toString());
		basisPersistenceHandler.send(new LogEntryResponse(logEntry2),channel);

		mConnection.execute(function);
		JCO.Table bapiRet2 = function.getTableParameterList().getTable("RETURN");
		for (int i = 0; i < bapiRet2.getNumRows(); i++, bapiRet2.nextRow()) {
			LogEntry logEntry = new LogEntry();
			if(bapiRet2.getString("TYPE").equals("S")||bapiRet2.getString("TYPE").equals("I"))
				logEntry.setLogLevel(Level.INFO);
			else if(bapiRet2.getString("TYPE").equals("W"))
				logEntry.setLogLevel(Level.WARNING);
			else
				logEntry.setLogLevel(Level.FATAL);
			logEntry.setLogDate(new Date());
			logEntry.setMessageComponent(sapTradeCapture.getName());
			logEntry.setMessageText(bapiRet2.getString("MESSAGE"));
			basisPersistenceHandler.send(new LogEntryResponse(logEntry),channel);
			
		}
		
		Field financialTransactionField = function.getExportParameterList().getField("FINANCIALTRANSACTION");
		
		if(test)
		{
			if(financialTransactionField.getString().length()==0)
			{
				sapTradeCaptureEntry.setExportStatus(ExportStatus.TEST_FAILED);
				sapTradeCaptureEntry.setSapFinancialTransaction(null);
			}
			else
			{
				sapTradeCaptureEntry.setExportStatus(ExportStatus.TEST_DONE);
				sapTradeCaptureEntry.setSapFinancialTransaction(financialTransactionField.getString());
			}
		}
		else
		{
			if(financialTransactionField.getString().length()==0)
			{
				sapTradeCaptureEntry.setSapFinancialTransaction(null);
				sapTradeCaptureEntry.setExportStatus(ExportStatus.FAILED);
			}
			else
			{
				sapTradeCaptureEntry.setSapFinancialTransaction(financialTransactionField.getString());
				sapTradeCaptureEntry.setExportStatus(ExportStatus.DONE);
			}
		}
		
		Function bapiCommit = functionTemplate2.getFunction();
		Field waitField = bapiCommit.getImportParameterList().getField("WAIT");
		waitField.setValue("X");
		
		mConnection.execute(bapiCommit);

	}

}
