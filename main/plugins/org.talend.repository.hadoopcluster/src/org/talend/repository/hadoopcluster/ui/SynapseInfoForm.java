// ============================================================================
//
// Copyright (C) 2006-2019 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.repository.hadoopcluster.ui;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.talend.commons.ui.swt.formtools.Form;
import org.talend.commons.ui.swt.formtools.LabelledCombo;
import org.talend.commons.ui.swt.formtools.LabelledText;
import org.talend.core.database.conn.ConnParameterKeys;
import org.talend.core.hadoop.version.ESynapseStorage;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.utils.ContextParameterUtils;
import org.talend.hadoop.distribution.model.DistributionBean;
import org.talend.hadoop.distribution.model.DistributionVersion;
import org.talend.metadata.managment.ui.utils.ExtendedNodeConnectionContextUtils.EHadoopParamName;
import org.talend.repository.hadoopcluster.i18n.Messages;
import org.talend.repository.hadoopcluster.ui.common.AbstractHadoopClusterInfoForm;
import org.talend.repository.hadoopcluster.util.HCRepositoryUtil;
import org.talend.repository.model.hadoopcluster.HadoopClusterConnection;
import org.talend.core.hadoop.version.ESynapseAuthType;

public class SynapseInfoForm extends AbstractHadoopClusterInfoForm<HadoopClusterConnection> {

	private Composite parentForm;
	
	private LabelledText synapseHostnameText;

    private LabelledText synapseTokenText;
    
    private LabelledText synapseSparkPoolsText;

    private Composite storagePartComposite;

    private LabelledCombo storageCombo;

    private LabelledText azureHostnameText;

    private LabelledText azureContainerText;

    private LabelledCombo storageAuthTypeCombo;
    
    private LabelledText azureUsernameText;

    private LabelledText azurePasswordText;
    
    private LabelledText azureClientIdText;
    
    private LabelledText azureDirectoryIdText;
    
    private LabelledText azureClientKeyText;

    private LabelledText azureDeployBlobText;
    
    private LabelledText driverMemoryText;
    
    private LabelledText driverCoresText;
    
    private LabelledText executorMemoryText;
    
    private Button tuningPropButton;
    
    protected Composite propertiesComposite;

    private boolean creation;
    
    public SynapseInfoForm(Composite parent, ConnectionItem connectionItem, String[] existingNames, boolean creation,
        DistributionBean hadoopDistribution, DistributionVersion hadoopVersison) {
        super(parent, SWT.NONE, existingNames);
        this.parentForm = parent;
        this.connectionItem = connectionItem;
        this.creation = creation;
        setConnectionItem(connectionItem);
        setupForm(true);
        init();
        setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout layout = (GridLayout) getLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        setLayout(layout);
    }

    @Override
    protected void initialize() {
        init();
    }

    @Override
    protected void updatePasswordFields() {
        if (!isContextMode()) {
            synapseTokenText.getTextControl().setEchoChar('*');
            azurePasswordText.getTextControl().setEchoChar('*');
        } else {
            if (synapseTokenText.getText().startsWith(ContextParameterUtils.JAVA_NEW_CONTEXT_PREFIX)) {
                synapseTokenText.getTextControl().setEchoChar('\0');
            }
            if (azurePasswordText.getText().startsWith(ContextParameterUtils.JAVA_NEW_CONTEXT_PREFIX)) {
                azurePasswordText.getTextControl().setEchoChar('\0');
            }
        }
    }

    @Override
    public void init() {
        if (isNeedFillDefaults()) {
            fillDefaults();
        }
        
        getConnection().getParameters().put(ConnParameterKeys.CONN_PARA_KEY_SPARK_MODE,"YARN_CLUSTER");
        
        String synapseHostName = StringUtils.trimToEmpty(getConnection().getParameters().get(ConnParameterKeys.CONN_PARA_KEY_SYNAPSE_HOST));
        synapseHostnameText.setText(synapseHostName);
        
        String synapseSparkPools = StringUtils.trimToEmpty(getConnection().getParameters().get(ConnParameterKeys.CONN_PARA_KEY_SYNAPSE_SPARK_POOLS));
        synapseSparkPoolsText.setText(synapseSparkPools);
        
        String synapseToken = StringUtils.trimToEmpty(getConnection().getParameters().get(ConnParameterKeys.CONN_PARA_KEY_SYNAPSE_AUTH_TOKEN));
        synapseTokenText.setText(synapseToken);
        
        String synapseStorage = StringUtils.trimToEmpty(getConnection().getParameters().get(ConnParameterKeys.CONN_PARA_KEY_SYNAPSE_FS_STORAGE));
        if (synapseStorage != null) {
        	ESynapseStorage storage = ESynapseStorage.getSynapseStorageByName(synapseStorage, false);
            if (storage != null) {
                storageCombo.setText(storage.getDisplayName());
            } else {
                storageCombo.select(0);
            }
        } else {
            storageCombo.select(0);
        }
        
        String authModeValue = StringUtils.trimToEmpty(getConnection().getParameters().get(ConnParameterKeys.CONN_PARA_KEY_SYNAPSE_AUTH_MODE));
        if (authModeValue != null) {
        	ESynapseAuthType type = ESynapseAuthType.getSynapseAuthTypeByName(authModeValue, false);
            if (type != null) {
            	storageAuthTypeCombo.setText(type.getDisplayName());
            } else {
            	storageAuthTypeCombo.select(0);
            }
        } else {
        	storageAuthTypeCombo.select(0);
        }
        
        String synapseFSHostname = StringUtils.trimToEmpty(getConnection().getParameters().get(ConnParameterKeys.CONN_PARA_KEY_SYNAPSE_FS_HOSTNAME));
        azureHostnameText.setText(synapseFSHostname);
        
        String synapseFSContainer = StringUtils.trimToEmpty(getConnection().getParameters().get(ConnParameterKeys.CONN_PARA_KEY_SYNAPSE_FS_CONTAINER));
        azureContainerText.setText(synapseFSContainer);
        
        String credentialName = storageAuthTypeCombo.getText();
        
        String synapseFSUsername = StringUtils.trimToEmpty(getConnection().getParameters().get(ConnParameterKeys.CONN_PARA_KEY_SYNAPSE_FS_USERNAME));
        azureUsernameText.setText(synapseFSUsername);
        azureUsernameText.setVisible(ESynapseAuthType.SECRETKEY.getDisplayName().equals(credentialName));
        
        String azurePassword = StringUtils.trimToEmpty(getConnection().getParameters().get(ConnParameterKeys.CONN_PARA_KEY_SYNAPSE_FS_PASSWORD));
        azurePasswordText.setText(azurePassword);
        azureUsernameText.setVisible(ESynapseAuthType.SECRETKEY.getDisplayName().equals(credentialName));
        
        String azureClientId = StringUtils.trimToEmpty(getConnection().getParameters().get(ConnParameterKeys.CONN_PARA_KEY_SYNAPSE_APPLICATION_ID));
        azureClientIdText.setText(azureClientId);
        azureClientIdText.setVisible(ESynapseAuthType.AAD.getDisplayName().equals(credentialName));
        
        String azureDirectoryId = StringUtils.trimToEmpty(getConnection().getParameters().get(ConnParameterKeys.CONN_PARA_KEY_SYNAPSE_DIRECTORY_ID));
        azureDirectoryIdText.setText(azureDirectoryId);
        azureDirectoryIdText.setVisible(ESynapseAuthType.AAD.getDisplayName().equals(credentialName));
        
        String azureClientKey = StringUtils.trimToEmpty(getConnection().getParameters().get(ConnParameterKeys.CONN_PARA_KEY_SYNAPSE_CLIENT_KEY));
        azureClientKeyText.setText(azureClientKey);
        azureClientKeyText.setVisible(ESynapseAuthType.AAD.getDisplayName().equals(credentialName));
        
        String azureDeployBlob = StringUtils.trimToEmpty(getConnection().getParameters().get(ConnParameterKeys.CONN_PARA_KEY_SYNAPSE_DEPLOY_BLOB));
        azureDeployBlobText.setText(azureDeployBlob);
        
        String driverMemory = StringUtils.trimToEmpty(getConnection().getParameters().get(ConnParameterKeys.CONN_PARA_KEY_DRIVER_MEMORY));
        driverMemoryText.setText(driverMemory);
        
        String driverCores = StringUtils.trimToEmpty(getConnection().getParameters().get(ConnParameterKeys.CONN_PARA_KEY_DRIVER_CORES));
        driverCoresText.setText(driverCores);
        
        String executorMemory = StringUtils.trimToEmpty(getConnection().getParameters().get(ConnParameterKeys.CONN_PARA_KEY_EXECUTOR_MEMORY));
        executorMemoryText.setText(executorMemory);
       
        boolean checkedTuningProp = Boolean.parseBoolean(getConnection().getParameters().get(ConnParameterKeys.CONN_PARA_KEY_TUNING_PROPERTIES));
        tuningPropButton.setSelection(checkedTuningProp);
        
        updatePasswordFields();
        updateStatus(IStatus.OK, EMPTY_STRING);
    }

    @Override
    protected void adaptFormToReadOnly() {
        readOnly = isReadOnly();
        synapseHostnameText.setReadOnly(readOnly);
        synapseSparkPoolsText.setReadOnly(readOnly);
        synapseTokenText.setReadOnly(readOnly);
        azureHostnameText.setReadOnly(readOnly);
        azureContainerText.setReadOnly(readOnly);
        azureUsernameText.setReadOnly(readOnly);
        azurePasswordText.setReadOnly(readOnly);
        azureClientIdText.setReadOnly(readOnly);
        azureDirectoryIdText.setReadOnly(readOnly);
        azureClientKeyText.setReadOnly(readOnly);
        azureDeployBlobText.setReadOnly(readOnly);
        driverMemoryText.setReadOnly(readOnly);
        driverCoresText.setReadOnly(readOnly);
        executorMemoryText.setReadOnly(readOnly);
        tuningPropButton.setEnabled(!readOnly);
    }

    @Override
    protected void updateEditableStatus(boolean isEditable) {
        synapseHostnameText.setEditable(isEditable);
        synapseSparkPoolsText.setEditable(isEditable);
        synapseTokenText.setEditable(isEditable);
        storageCombo.setEnabled(isEditable);
        azureHostnameText.setEditable(isEditable);
        azureContainerText.setEditable(isEditable);
        azureUsernameText.setEditable(isEditable);
        azurePasswordText.setEditable(isEditable);
        azureClientIdText.setEditable(isEditable);
        azureDirectoryIdText.setEditable(isEditable);
        azureClientKeyText.setEditable(isEditable);
        azureDeployBlobText.setEditable(isEditable);
        driverMemoryText.setEditable(isEditable);
        driverCoresText.setEditable(isEditable);
        executorMemoryText.setEditable(isEditable);
        tuningPropButton.setEnabled(isEditable);
        ((HadoopClusterForm) this.getParent()).updateEditableStatus(isEditable);
    }

    @Override
    protected void addFields() {
    	addSynapseFields();
    	addFSFields();
    	addTuningFields();
        propertiesComposite = new Composite(this, SWT.NONE);
        GridLayout propertiesLayout = new GridLayout(3, false);
        propertiesLayout.marginWidth = 0;
        propertiesLayout.marginHeight = 0;
        propertiesComposite.setLayout(propertiesLayout);
        propertiesComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    }

    private void addSynapseFields() {
    	Group synapseGroup = Form.createGroup(this, 2, Messages.getString("SynapseInfoForm.synapseSettings"), 110);
        synapseGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        synapseHostnameText = new LabelledText(synapseGroup, Messages.getString("SynapseInfoForm.text.synapse.hostname"), 1);
        synapseSparkPoolsText = new LabelledText(synapseGroup, Messages.getString("SynapseInfoForm.text.synapse.sparkPools"), 1);
        synapseTokenText = new LabelledText(synapseGroup,Messages.getString("SynapseInfoForm.text.synapse.token"), 1, SWT.PASSWORD | SWT.BORDER | SWT.SINGLE);
        synapseTokenText.getTextControl().setEchoChar('*');
    }

    private void addFSFields() {
       // Group azureGroup = Form.createGroup(this, 1, Messages.getString("SynapseInfoForm.azureSettings"), 110); //$NON-NLS-1$
    	 Group azureGroup = Form.createGroup(this, 2, Messages.getString("SynapseInfoForm.azureSettings"), 110); //$NON-NLS-1$
        azureGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        storagePartComposite = new Composite(azureGroup, SWT.NULL);
        GridLayout storagePartLayout = new GridLayout(4, false);
        storagePartLayout.marginWidth = 0;
        storagePartLayout.marginHeight = 0;
        storagePartComposite.setLayout(storagePartLayout);
        storagePartComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        storageCombo = new LabelledCombo(storagePartComposite, Messages.getString("SynapseInfoForm.text.azure.storage"),
                Messages.getString("SynapseInfoForm.text.azure.storage.tip"),
                ESynapseStorage.getAllSynapseStorageDisplayNames().toArray(new String[0]), 1, true);

        /*Composite azurePartComposite = new Composite(azureGroup, SWT.NULL);
        GridLayout azurePartLayout = new GridLayout(4, false);
        azurePartLayout.marginWidth = 0;
        azurePartLayout.marginHeight = 0;
        azurePartComposite.setLayout(azurePartLayout);
        azurePartComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));*/

        /*azureHostnameText = new LabelledText(azurePartComposite, Messages.getString("SynapseInfoForm.text.azure.hostname"), 1); //$NON-NLS-1$
        
        storageAuthTypeCombo = new LabelledCombo(azurePartComposite, Messages.getString("SynapseInfoForm.text.authentication"), "", //$NON-NLS-1$ $NON-NLS-2$
        		ESynapseAuthType.getAllSynapseAuthTypes());
        
        azureContainerText = new LabelledText(azurePartComposite, Messages.getString("SynapseInfoForm.text.azure.container"), 1); //$NON-NLS-1$
        
        azureUsernameText = new LabelledText(azurePartComposite, Messages.getString("SynapseInfoForm.text.azure.username"), 1); //$NON-NLS-1$
        
        azurePasswordText = new LabelledText(azurePartComposite,
                Messages.getString("SynapseInfoForm.text.azure.password"), 1, SWT.PASSWORD | SWT.BORDER | SWT.SINGLE); //$NON-NLS-1$
        
        azurePasswordText.getTextControl().setEchoChar('*');
        
        azureClientIdText = new LabelledText(azurePartComposite, Messages.getString("SynapseInfoForm.text.azure.clientId"), 1);
        
        azureDirectoryIdText = new LabelledText(azurePartComposite, Messages.getString("SynapseInfoForm.text.azure.directoryId"), 1);
        
        azureClientKeyText = new LabelledText(azurePartComposite,
                Messages.getString("SynapseInfoForm.text.azure.clientKey"), 1, SWT.PASSWORD | SWT.BORDER | SWT.SINGLE);
        azureClientKeyText.getTextControl().setEchoChar('*');
        
        azureDeployBlobText = new LabelledText(azurePartComposite, Messages.getString("SynapseInfoForm.text.azure.deployBlob"), 1);*/
        
        azureHostnameText = new LabelledText(storagePartComposite, Messages.getString("SynapseInfoForm.text.azure.hostname"), 1); //$NON-NLS-1$
        
        storageAuthTypeCombo = new LabelledCombo(storagePartComposite, Messages.getString("SynapseInfoForm.text.authentication"), "", //$NON-NLS-1$ $NON-NLS-2$
        		ESynapseAuthType.getAllSynapseAuthTypes());
        
        azureContainerText = new LabelledText(storagePartComposite, Messages.getString("SynapseInfoForm.text.azure.container"), 1); //$NON-NLS-1$
        
        azureDeployBlobText = new LabelledText(storagePartComposite, Messages.getString("SynapseInfoForm.text.azure.deployBlob"), 1);
        
        azureUsernameText = new LabelledText(storagePartComposite, Messages.getString("SynapseInfoForm.text.azure.username"), 1); //$NON-NLS-1$
        
        azurePasswordText = new LabelledText(storagePartComposite,
                Messages.getString("SynapseInfoForm.text.azure.password"), 1, SWT.PASSWORD | SWT.BORDER | SWT.SINGLE); //$NON-NLS-1$
        
        azurePasswordText.getTextControl().setEchoChar('*');
        
        azureClientIdText = new LabelledText(storagePartComposite, Messages.getString("SynapseInfoForm.text.azure.clientId"), 1);
        
        azureDirectoryIdText = new LabelledText(storagePartComposite, Messages.getString("SynapseInfoForm.text.azure.directoryId"), 1);
        
        azureClientKeyText = new LabelledText(storagePartComposite,
                Messages.getString("SynapseInfoForm.text.azure.clientKey"), 1, SWT.PASSWORD | SWT.BORDER | SWT.SINGLE);
        azureClientKeyText.getTextControl().setEchoChar('*');
        
    }
    
    private void addTuningFields() {
    	Group tuningGroup = Form.createGroup(this, 4, Messages.getString("SynapseInfoForm.tuningSettings"), 110);
    	tuningGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
    	
    	tuningPropButton = new Button(tuningGroup, SWT.CHECK);
    	tuningPropButton.setText(Messages.getString("SynapseInfoForm.useTuningProperties"));
    	tuningPropButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false, 2, 1));
        
        driverMemoryText = new LabelledText(tuningGroup, Messages.getString("SynapseInfoForm.text.synapse.driverMemory"), 1); //$NON-NLS-1$
        driverCoresText = new LabelledText(tuningGroup, Messages.getString("SynapseInfoForm.text.synapse.driverCores"), 1); //$NON-NLS-1$;
        executorMemoryText = new LabelledText(tuningGroup, Messages.getString("SynapseInfoForm.text.synapse.executorMemory"), 1); //$NON-NLS-1$;       
    }
    
    @Override
    protected void addFieldsListeners() {
        synapseHostnameText.addModifyListener(new ModifyListener() {
        	@Override
            public void modifyText(final ModifyEvent e) {
                getConnection().getParameters().put(ConnParameterKeys.CONN_PARA_KEY_SYNAPSE_HOST, synapseHostnameText.getText());
        		checkFieldsValue();
            }
        });
        
        synapseSparkPoolsText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(final ModifyEvent e) {
                getConnection().getParameters().put(ConnParameterKeys.CONN_PARA_KEY_SYNAPSE_SPARK_POOLS, synapseSparkPoolsText.getText());
            	checkFieldsValue();
            }
        });
        
        synapseTokenText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(final ModifyEvent e) {
                getConnection().getParameters().put(ConnParameterKeys.CONN_PARA_KEY_SYNAPSE_AUTH_TOKEN, synapseTokenText.getText());
                checkFieldsValue();
            }
        });

        storageCombo.getCombo().addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String storage = storageCombo.getText();
                getConnection().getParameters().put(ConnParameterKeys.CONN_PARA_KEY_SYNAPSE_FS_STORAGE,
                        ESynapseStorage.getSynapseStoragenByDisplayName(storage).getName());
                checkFieldsValue();
            }
        });

        azureHostnameText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(final ModifyEvent e) {
                getConnection().getParameters().put(ConnParameterKeys.CONN_PARA_KEY_SYNAPSE_FS_HOSTNAME, azureHostnameText.getText());
                checkFieldsValue();
            }
        });
        
        azureContainerText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(final ModifyEvent e) {
                getConnection().getParameters()
                        .put(ConnParameterKeys.CONN_PARA_KEY_SYNAPSE_FS_CONTAINER, azureContainerText.getText());
                checkFieldsValue();
            }
        });
        
        storageAuthTypeCombo.getCombo().addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String credentialName = storageAuthTypeCombo.getText();
                getConnection().getParameters().put(ConnParameterKeys.CONN_PARA_KEY_SYNAPSE_AUTH_MODE,
                    	ESynapseAuthType.getSynapseAuthTypeByDisplayName(credentialName).getName());
                
                azureUsernameText.setVisible(ESynapseAuthType.SECRETKEY.getDisplayName().equals(credentialName));
                azurePasswordText.setVisible(ESynapseAuthType.SECRETKEY.getDisplayName().equals(credentialName));
                
                azureClientIdText.setVisible(ESynapseAuthType.AAD.getDisplayName().equals(credentialName));
                azureDirectoryIdText.setVisible(ESynapseAuthType.AAD.getDisplayName().equals(credentialName));
                azureClientKeyText.setVisible(ESynapseAuthType.AAD.getDisplayName().equals(credentialName));
                checkFieldsValue();
            }
        });
        
        azureUsernameText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(final ModifyEvent e) {
                getConnection().getParameters().put(ConnParameterKeys.CONN_PARA_KEY_SYNAPSE_FS_USERNAME, azureUsernameText.getText());
                checkFieldsValue();
            }
        });
        
        azurePasswordText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(final ModifyEvent e) {
                getConnection().getParameters().put(ConnParameterKeys.CONN_PARA_KEY_SYNAPSE_FS_PASSWORD, azurePasswordText.getText());
                checkFieldsValue();
            }
        });
        
        azureClientIdText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(final ModifyEvent e) {
                getConnection().getParameters().put(ConnParameterKeys.CONN_PARA_KEY_SYNAPSE_FS_USERNAME, azureClientIdText.getText());
                checkFieldsValue();
            }
        });
        
        azureDirectoryIdText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(final ModifyEvent e) {
                getConnection().getParameters().put(ConnParameterKeys.CONN_PARA_KEY_SYNAPSE_DIRECTORY_ID, azureDirectoryIdText.getText());
                checkFieldsValue();
            }
        });
        
        azureClientKeyText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(final ModifyEvent e) {
                getConnection().getParameters().put(ConnParameterKeys.CONN_PARA_KEY_SYNAPSE_CLIENT_KEY, azureClientKeyText.getText());
                checkFieldsValue();
            }
        });
        
        azureDeployBlobText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(final ModifyEvent e) {
                getConnection().getParameters().put(ConnParameterKeys.CONN_PARA_KEY_SYNAPSE_DEPLOY_BLOB, azureDeployBlobText.getText());
                checkFieldsValue();
            }
        });
        
        tuningPropButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            	String selection = String.valueOf(tuningPropButton.getSelection());            	
            	getConnection().getParameters().put(ConnParameterKeys.CONN_PARA_KEY_TUNING_PROPERTIES, selection);
            	
            	driverMemoryText.setVisible(tuningPropButton.getSelection());
            	driverCoresText.setVisible(tuningPropButton.getSelection());
            	executorMemoryText.setVisible(tuningPropButton.getSelection());
            	checkFieldsValue();
            }
        });
        
        driverMemoryText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(final ModifyEvent e) {
                getConnection().getParameters().put(ConnParameterKeys.CONN_PARA_KEY_DRIVER_MEMORY, driverMemoryText.getText());
                driverMemoryText.setVisible(tuningPropButton.getSelection());
                checkFieldsValue();
            }
        });
        
        driverCoresText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(final ModifyEvent e) {
                getConnection().getParameters().put(ConnParameterKeys.CONN_PARA_KEY_DRIVER_CORES, driverCoresText.getText());
                driverCoresText.setVisible(tuningPropButton.getSelection());
                checkFieldsValue();
            }
        });
        
        executorMemoryText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(final ModifyEvent e) {
                getConnection().getParameters().put(ConnParameterKeys.CONN_PARA_KEY_EXECUTOR_MEMORY, executorMemoryText.getText());
                executorMemoryText.setVisible(tuningPropButton.getSelection());
                checkFieldsValue();
            }
        });
    }

    @Override
    public void updateForm() {
        adaptFormToEditable();
      
      }
    
    @Override
    public boolean checkFieldsValue() {
        if (!validText(synapseHostnameText.getText())) {
            updateStatus(IStatus.ERROR, Messages.getString("SynapseInfoForm.check.synapse.hostname")); //$NON-NLS-1$
            return false;
        }
        if (!validText(synapseTokenText.getText())) {
            updateStatus(IStatus.ERROR, Messages.getString("SynapseInfoForm.check.synapse.token")); //$NON-NLS-1$
            return false;
        }
        if (!validText(synapseSparkPoolsText.getText())) {
            updateStatus(IStatus.ERROR, Messages.getString("SynapseInfoForm.check.synapse.sparkPools")); //$NON-NLS-1$
            return false;
        }
        if (!validText(azureHostnameText.getText())) {
            updateStatus(IStatus.ERROR, Messages.getString("SynapseInfoForm.check.azure.hostname")); //$NON-NLS-1$
            return false;
        }
        if (!validText(azureContainerText.getText())) {
            updateStatus(IStatus.ERROR, Messages.getString("SynapseInfoForm.check.azure.container")); //$NON-NLS-1$
            return false;
        }
        if (!validText(azureUsernameText.getText())) {
            updateStatus(IStatus.ERROR, Messages.getString("SynapseInfoForm.check.azure.username")); //$NON-NLS-1$
            return false;
        }
        if (!validText(azurePasswordText.getText())) {
            updateStatus(IStatus.ERROR, Messages.getString("SynapseInfoForm.check.azure.password")); //$NON-NLS-1$
            return false;
        }
        if (!validText(azureClientIdText.getText())) {
            updateStatus(IStatus.ERROR, Messages.getString("SynapseInfoForm.check.azure.clientId")); //$NON-NLS-1$
            return false;
        }
        if (!validText(azureDirectoryIdText.getText())) {
            updateStatus(IStatus.ERROR, Messages.getString("SynapseInfoForm.check.azure.directoryId")); //$NON-NLS-1$
            return false;
        }
        if (!validText(azureClientKeyText.getText())) {
            updateStatus(IStatus.ERROR, Messages.getString("SynapseInfoForm.check.azure.clientKey")); //$NON-NLS-1$
            return false;
        }
        if (!validText(azureDeployBlobText.getText())) {
            updateStatus(IStatus.ERROR, Messages.getString("SynapseInfoForm.check.azure.deployBlob")); //$NON-NLS-1$
            return false;
        }
        if (!validText(driverMemoryText.getText())) {
            updateStatus(IStatus.ERROR, Messages.getString("SynapseInfoForm.check.synapse.driverMemory")); //$NON-NLS-1$
            return false;
        }
        if (!validText(driverCoresText.getText())) {
            updateStatus(IStatus.ERROR, Messages.getString("SynapseInfoForm.check.synapse.driverCores")); //$NON-NLS-1$
            return false;
        }
        if (!validText(executorMemoryText.getText())) {
            updateStatus(IStatus.ERROR, Messages.getString("SynapseInfoForm.check.synapse.executorMemory")); //$NON-NLS-1$
            return false;
        }

        updateStatus(IStatus.OK, null);
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.swt.widgets.Control#setVisible(boolean)
     */
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        updateForm();
        if (isReadOnly() != readOnly) {
            adaptFormToReadOnly();
        }
        if (visible) {
            updateStatus(getStatusLevel(), getStatus());
        }
    }

    @Override
    protected void collectConParameters() {
    	collectSynapseParameters(true);
        collectKeyAzureParameters(true);
        collectTuningParameters(true);
    }

    private void collectSynapseParameters(boolean isUse) {
        addContextParams(EHadoopParamName.SynapseHostName, isUse);
        addContextParams(EHadoopParamName.SynapseAuthToken, isUse);
        addContextParams(EHadoopParamName.SynapseSparkPools, isUse);        
    }

    private void collectKeyAzureParameters(boolean isUse) {
        addContextParams(EHadoopParamName.SynapseFsHostName, isUse);
        addContextParams(EHadoopParamName.SynapseFsUserName, isUse);
        addContextParams(EHadoopParamName.SynapseFsPassword, isUse);
        addContextParams(EHadoopParamName.SynapseFsContainer, isUse);
        addContextParams(EHadoopParamName.SynapseDeployBlob, isUse);
    }
    
    private void collectTuningParameters(boolean isUse) {
    	addContextParams(EHadoopParamName.UseTuningProperties, isUse);
        addContextParams(EHadoopParamName.SynapseDriverMemory, isUse);
        addContextParams(EHadoopParamName.SynapseDriverCores, isUse);
        addContextParams(EHadoopParamName.SynapseExecutorMemory, isUse);
    }

    private void fillDefaults() {
        HadoopClusterConnection connection = getConnection();
        if (creation && !connection.isUseCustomConfs()) {
            HCRepositoryUtil.fillDefaultValuesOfHadoopCluster(connection);
        }
    }
}
