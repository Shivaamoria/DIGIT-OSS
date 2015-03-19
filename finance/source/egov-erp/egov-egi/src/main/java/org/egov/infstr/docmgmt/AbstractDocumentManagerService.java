/*
 * @(#)AbstractDocumentManagerService.java 3.0, 17 Jun, 2013 11:53:49 AM
 * Copyright 2013 eGovernments Foundation. All rights reserved. 
 * eGovernments PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.egov.infstr.docmgmt;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jcr.ImportUUIDBehavior;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

import org.egov.exceptions.EGOVRuntimeException;
import org.egov.infra.admin.master.entity.User;
import org.egov.infstr.ValidationException;
import org.egov.infstr.config.AppConfigValues;
import org.egov.infstr.config.dao.AppConfigValuesDAO;
import org.egov.infstr.services.PersistenceService;
import org.egov.infstr.services.ScriptService;
import org.egov.infstr.utils.SequenceNumberGenerator;
import org.springmodules.jcr.support.JcrDaoSupport;
import org.xml.sax.ContentHandler;

/**
 * The Class AbstractDocumentManagerService.
 * Abstract implementation of DocumentManagerService. Common business logic for Document Management<br/>
 * held at this class, Sub class this to implement Jackrabbit Document Management System. 
 * @param <T> the generic type
 */
public abstract class AbstractDocumentManagerService<T extends DocumentObject> extends JcrDaoSupport implements DocumentManagerService<T> {
	
	private transient AppConfigValuesDAO appConfValDao;
	private transient ScriptService scriptExecuter;
	private transient SequenceNumberGenerator sequenceNumberGenerator;
	private transient PersistenceService<User, Integer> userPersistenceService;
	
	/**
	 * Gets the AppConfigValuesDAO.
	 * @return the AppConfigValuesDAO
	 */
	public AppConfigValuesDAO getAppConfigValuesDAO() {
		return this.appConfValDao;
	}
	
	/**
	 * Sets the AppConfigValuesDAO.
	 * @param appConfigValuesDAO the new AppConfigValuesDAO.
	 */
	public void setAppConfigValuesDAO(final AppConfigValuesDAO appConfValDao) {
		this.appConfValDao = appConfValDao;
	}
	
	/**
	 * Gets the ScriptService.
	 * @return the ScriptService instance
	 */
	public ScriptService getScriptExecuter() {
		return this.scriptExecuter;
	}
	
	/**
	 * Sets the ScriptService.
	 * @param scriptExecuter the new ScriptService
	 */
	public void setScriptExecuter(final ScriptService scriptExecuter) {
		this.scriptExecuter = scriptExecuter;
	}
	
	/**
	 * Gets the SequenceGenerator.
	 * @return the SequenceGenerator instance
	 */
	public SequenceNumberGenerator getSequenceNumberGenerator() {
		return this.sequenceNumberGenerator;
	}
	
	/**
	 * Sets the SequenceGenerator.
	 * @param sequenceGenerator the new SequenceGenerator
	 */
	public void setSequenceNumberGenerator(final SequenceNumberGenerator sequenceNumberGenerator) {
		this.sequenceNumberGenerator = sequenceNumberGenerator;
	}
	
	/**
	 * Gets the User Dao Service.
	 * @return the User Dao Service
	 */
	public PersistenceService<User, Integer> getUserPersistenceService() {
		return this.userPersistenceService;
	}
	
	/**
	 * Sets the User Dao Service.
	 * @param userService the new User Dao Service
	 */
	public void setUserPersistenceService(final PersistenceService<User, Integer> userPersistenceService) {
		this.userPersistenceService = userPersistenceService;
	}
	
	/**
	 * Checks if is auto generate doc number.
	 * @return true, if is auto generate doc number {@inheritDoc DocumentManagerService}
	 */
	@Override
	public boolean isAutoGenerateDocNumber() {
		// Getting value to check Auto Document Number Generation is required or not
		final AppConfigValues appConfigVal = this.appConfValDao.getAppConfigValueByDate("egi", "AUTO_DOC_NUM", new Date());
		return (appConfigVal != null && "Y".equalsIgnoreCase(appConfigVal.getValue()));
	}
	
	/**
	 * Generate the document number.
	 * @return the string {@inheritDoc DocumentManagerService}
	 */
	@Override
	public String generateDocumentNumber() {
		return this.scriptExecuter.executeScript("egi.docnumber.generator", ScriptService.createContext("sequenceGenerator", this.sequenceNumberGenerator)).toString();
	}
	
	/**
	 * Gives the DocumentObject jcr Node for the given documentNumber.
	 * @param docNumber the doc number
	 * @param moduleName the module name
	 * @return Node
	 */
	protected Node getDocumentNode(final String docNumber, final String moduleName, final String domainName) {
		Node documentNode = null;
		try {
			//TODO Move from XPATH query to JQOM or JSQL2
			final StringBuffer queryStr = new StringBuffer(PATH_SEPARATOR);
			queryStr.append(PATH_SEPARATOR).append(DocumentObject.escapeSpecialChars(domainName)).append(PATH_SEPARATOR).append(moduleName).append(PATH_SEPARATOR).append(NODE_DOC_OBJECT);
			queryStr.append("[@").append(PROP_DOC_NUM).append(" = '").append(docNumber).append("' and @").append(PROP_MODULE_NAME).append(" = '").append(moduleName).append("']");
			final QueryResult queryResult = this.getSession().getWorkspace().getQueryManager().createQuery(queryStr.toString(), Query.XPATH).execute();
			final NodeIterator nodeIterator = queryResult.getNodes();
			if (nodeIterator.hasNext()) {
				documentNode = nodeIterator.nextNode();
			}
		} catch (final InvalidQueryException e) {
			this.logger.error("Error occurred while trying to fetch Document, Query not valid.", e);
			throw new EGOVRuntimeException("Error occurred while trying to fetch Document, Query not valid.", e);
		} catch (final RepositoryException e) {
			this.logger.error("Error occurred while trying to fetch Document.", e);
			throw new EGOVRuntimeException("Error occurred while trying to fetch Document.", e);
		}
		return documentNode;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void exportDocument(final String filePath, final String nodePath, final boolean skipBinary, final boolean noRecurse) {
		try {
			final BufferedOutputStream outStream = new BufferedOutputStream(new FileOutputStream(filePath));
			final ContentHandler handler = new org.apache.xml.serialize.XMLSerializer(outStream, null).asContentHandler();
			this.getSession().exportDocumentView(nodePath, handler, skipBinary, noRecurse);
			outStream.flush();
			outStream.close();
		} catch (final Exception e) {
			this.logger.error("Error occurred while trying to export Document", e);
			throw new EGOVRuntimeException("Error occurred while trying to export Document to File : " + filePath + " from Document Path : " + nodePath, e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void importDocument(final String filePath, final String nodePath) {
		try {
			final BufferedInputStream inStream = new BufferedInputStream(new FileInputStream(filePath));
			this.getSession().importXML(nodePath, inStream, ImportUUIDBehavior.IMPORT_UUID_CREATE_NEW);
			this.getSession().save();
			inStream.close();
		} catch (final Exception e) {
			this.logger.error("Error occurred while trying to import Document", e);
			throw new EGOVRuntimeException("Error occurred while trying to import Document from File : " + filePath + " to Document Path : " + nodePath, e);
		}
	}
	
	/**
	 * Checks the mandatory property are set before saving to Content Repo.
	 * @param documentObject the document object
	 * @throws Exception the exception
	 */
	protected void checkMandatoryProperty(final T documentObject) throws IllegalAccessException, RuntimeException {
		final Field[] fields = documentObject.getClass().getDeclaredFields();
		final List<String> fieldNames = new ArrayList<String>();
		for (final Field field : fields) {
			if (field.isAnnotationPresent(org.apache.jackrabbit.ocm.mapper.impl.annotation.Field.class)) {
				if (!field.isAccessible()) {
					field.setAccessible(true);
				}
				final org.apache.jackrabbit.ocm.mapper.impl.annotation.Field fieldAnnotation = field.getAnnotation(org.apache.jackrabbit.ocm.mapper.impl.annotation.Field.class);
				if (fieldAnnotation.jcrMandatory()) {
					final Object value = field.get(documentObject);
					if (value == null || (value instanceof String && value.toString().equals(""))) {
						fieldNames.add(field.getName());
					}
				}
			}
		}
		if (!fieldNames.isEmpty()) {
			throw new ValidationException(PROP_DOC_NUM, "DocMngr.Mandtory.Missing", documentObject.getClass().getSimpleName(), fieldNames.toString());
		}
	}
}
