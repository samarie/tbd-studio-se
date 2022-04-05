package org.talend.repository.hadoopcluster.ui;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
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
import org.talend.commons.ui.swt.formtools.LabelledFileField;
import org.talend.commons.ui.swt.formtools.LabelledText;
import org.talend.commons.ui.swt.formtools.UtilsButton;
import org.talend.core.database.conn.ConnParameterKeys;
import org.talend.core.hadoop.version.EAuthenticationMode;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.utils.ContextParameterUtils;
import org.talend.designer.core.model.utils.emf.talendfile.ContextType;
import org.talend.designer.hdfsbrowse.hadoop.service.EHadoopServiceType;
import org.talend.designer.hdfsbrowse.hadoop.service.HadoopServiceProperties;
import org.talend.designer.hdfsbrowse.hadoop.service.check.CheckHadoopServicesDialog;
import org.talend.hadoop.distribution.model.DistributionBean;
import org.talend.hadoop.distribution.model.DistributionVersion;
import org.talend.metadata.managment.ui.utils.ConnectionContextHelper;
import org.talend.metadata.managment.ui.utils.ExtendedNodeConnectionContextUtils.EHadoopParamName;
import org.talend.repository.hadoopcluster.i18n.Messages;
import org.talend.repository.hadoopcluster.ui.common.AbstractHadoopClusterInfoForm;
import org.talend.repository.hadoopcluster.util.HCRepositoryUtil;
import org.talend.repository.model.hadoopcluster.HadoopClusterConnection;


public class KnoxInfoForm extends AbstractHadoopClusterInfoForm<HadoopClusterConnection> {
    
    private LabelledText knoxURLText;
    private LabelledText knoxUserText;
    private LabelledText knoxPasswordText;
    private LabelledText knoxDirectoryText;
    
    private Group authGroup;
    private Composite authPartComposite;
    private Composite authCommonComposite;
    private Button kerberosBtn;
    private Composite authNodejtOrRmHistoryComposite;
    private LabelledText namenodePrincipalText;
    private LabelledText jtOrRmPrincipalText;
    private LabelledText jobHistoryPrincipalText;
    private LabelledText keytabPrincipalText;
    private Composite authKeytabComposite;
    private Button keytabBtn;
    private LabelledFileField keytabText;
    
    private ScrolledComposite scrolledComposite;

    private UtilsButton checkServicesBtn;
    
    private final boolean creation;

    protected KnoxInfoForm(Composite parent, ConnectionItem connectionItem, String[] existingNames, boolean creation,
            DistributionBean hadoopDistribution, DistributionVersion hadoopVersison) {
        super(parent, SWT.NONE, existingNames);
        this.connectionItem = connectionItem;
        this.creation = creation;
        setupForm(true);
        init();
        setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout layout = (GridLayout) getLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        setLayout(layout);
    }
    
    private void addConfigurationFields() {
        Group configGroup = Form.createGroup(this, 2, Messages.getString("KnoxInfoForm.text.configuration"), 110); //$NON-NLS-1$
        configGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        knoxURLText = new LabelledText(configGroup, Messages.getString("KnoxInfoForm.text.knoxURL"), 1); //$NON-NLS-1$ $NON-NLS-2$
        knoxUserText = new LabelledText(configGroup, Messages.getString("KnoxInfoForm.text.knoxUser"), 1); //$NON-NLS-1$

        knoxPasswordText = new LabelledText(configGroup, Messages.getString("KnoxInfoForm.text.knoxPassword"), 1); //$NON-NLS-1$
        knoxPasswordText.getTextControl().setEchoChar('*');

        knoxDirectoryText = new LabelledText(configGroup, Messages.getString("KnoxInfoForm.text.knoxDirectory"), 1); //$NON-NLS-1$
    }
    
    
    
    private void addAuthenticationFields() {
        authGroup = Form.createGroup(this, 1, Messages.getString("HadoopClusterForm.authenticationSettings"), 110); //$NON-NLS-1$
        authGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        authPartComposite = new Composite(authGroup, SWT.NULL);
        GridLayout authPartLayout = new GridLayout(1, false);
        authPartLayout.marginWidth = 0;
        authPartLayout.marginHeight = 0;
        authPartComposite.setLayout(authPartLayout);
        authPartComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        authCommonComposite = new Composite(authPartComposite, SWT.NULL);
        GridLayout authCommonCompLayout = new GridLayout(1, false);
        authCommonCompLayout.marginWidth = 0;
        authCommonCompLayout.marginHeight = 0;
        authCommonComposite.setLayout(authCommonCompLayout);
        authCommonComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        kerberosBtn = new Button(authCommonComposite, SWT.CHECK);
        kerberosBtn.setText(Messages.getString("HadoopClusterForm.button.kerberos")); //$NON-NLS-1$
        kerberosBtn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 4, 1));

        authNodejtOrRmHistoryComposite = new Composite(authCommonComposite, SWT.NULL);
        GridLayout authNodejtOrRmHistoryCompLayout = new GridLayout(4, false);
        authNodejtOrRmHistoryCompLayout.marginWidth = 0;
        authNodejtOrRmHistoryCompLayout.marginHeight = 0;
        authNodejtOrRmHistoryComposite.setLayout(authNodejtOrRmHistoryCompLayout);
        authNodejtOrRmHistoryComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        namenodePrincipalText = new LabelledText(authNodejtOrRmHistoryComposite,
                Messages.getString("HadoopClusterForm.text.namenodePrincipal"), 1); //$NON-NLS-1$
        jtOrRmPrincipalText = new LabelledText(authNodejtOrRmHistoryComposite,
                Messages.getString("HadoopClusterForm.text.rmPrincipal"), 1); //$NON-NLS-1$
        jobHistoryPrincipalText = new LabelledText(authNodejtOrRmHistoryComposite,
                Messages.getString("HadoopClusterForm.text.jobHistoryPrincipal"), 1); //$NON-NLS-1$

        authKeytabComposite = new Composite(authPartComposite, SWT.NULL);
        GridLayout authKeytabCompLayout = new GridLayout(5, false);
        authKeytabCompLayout.marginWidth = 0;
        authKeytabCompLayout.marginHeight = 0;
        authKeytabComposite.setLayout(authKeytabCompLayout);
        authKeytabComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        keytabBtn = new Button(authKeytabComposite, SWT.CHECK);
        keytabBtn.setText(Messages.getString("HadoopClusterForm.button.keytab")); //$NON-NLS-1$
        keytabBtn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 5, 1));
        keytabPrincipalText = new LabelledText(authKeytabComposite,
                Messages.getString("HadoopClusterForm.text.keytabPrincipal"), 1); //$NON-NLS-1$
        String[] extensions = { "*.*" }; //$NON-NLS-1$
        keytabText = new LabelledFileField(authKeytabComposite, Messages.getString("HadoopClusterForm.text.keytab"), extensions); //$NON-NLS-1$
    }
    
    private void hideKerberosControl(boolean hide) {
        hideControl(authNodejtOrRmHistoryComposite, hide);
        hideControl(authKeytabComposite, hide);
        authCommonComposite.layout();
        authCommonComposite.getParent().layout();
    }
    
    private void addCheckFields() {
        Composite checkGroup = new Composite(this, SWT.NONE);
        GridLayout checkGridLayout = new GridLayout(1, false);
        checkGroup.setLayout(checkGridLayout);
        GridData checkGridData = new GridData(GridData.FILL_HORIZONTAL);
        checkGridData.minimumHeight = 5;
        checkGroup.setLayoutData(checkGridData);
        Composite checkButtonComposite = Form.startNewGridLayout(checkGroup, 1, false, SWT.CENTER, SWT.BOTTOM);
        GridLayout checkButtonLayout = (GridLayout) checkButtonComposite.getLayout();
        checkButtonLayout.marginHeight = 0;
        checkButtonLayout.marginWidth = 0;
        checkServicesBtn = new UtilsButton(checkButtonComposite, Messages.getString("HadoopClusterForm.button.check"), true); //$NON-NLS-1$
        checkServicesBtn.setEnabled(false);
    }
    
    @Override
    protected void addUtilsButtonListeners() {
        checkServicesBtn.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                checkServices();
            }
        });
    }
    
    private void checkServices() {
        Map<EHadoopServiceType, HadoopServiceProperties> serviceTypeToProperties = new HashMap<EHadoopServiceType, HadoopServiceProperties>();
        
        HadoopServiceProperties nnProperties = new HadoopServiceProperties();
        initCommonProperties(nnProperties);
        nnProperties.setKnoxURL(knoxURLText.getText());
        nnProperties.setKnoxUser(knoxUserText.getText());
        nnProperties.setKnoxPassword(knoxPasswordText.getText());
        nnProperties.setKnoxDirectory(knoxDirectoryText.getText());
        
        serviceTypeToProperties.put(EHadoopServiceType.KNOX_RESOURCE_MANAGER, nnProperties);
        serviceTypeToProperties.put(EHadoopServiceType.KNOX_NAMENODE, nnProperties);
        new CheckHadoopServicesDialog(getShell(), serviceTypeToProperties).open();
    }
    
    private void initCommonProperties(HadoopServiceProperties properties) {
        HadoopClusterConnection connection = getConnection();
        ContextType contextType = null;
        if (getConnection().isContextMode()) {
            contextType = ConnectionContextHelper.getContextTypeForContextMode(connection, connection.getContextName(), false);
        }
        properties.setContextType(contextType);
        properties.setRelativeHadoopClusterId(connectionItem.getProperty().getId());
        properties.setRelativeHadoopClusterLabel(connectionItem.getProperty().getLabel());
        properties.setDistribution(connection.getDistribution());
        properties.setVersion(connection.getDfVersion());
        properties.setUseKrb(connection.isEnableKerberos());
    }
    
    @Override
    public boolean checkFieldsValue() {
        checkServicesBtn.setEnabled(false);
        if (!validText(knoxURLText.getText())) {
            updateStatus(IStatus.ERROR, Messages.getString("KnoxInfoForm.check.configuration.knoxURL")); //$NON-NLS-1$
            return false;
        }
        if (!validText(knoxUserText.getText())) {
            updateStatus(IStatus.ERROR, Messages.getString("KnoxInfoForm.check.configuration.knoxUser")); //$NON-NLS-1$
            return false;
        }
        if (!validText(knoxPasswordText.getText())) {
            updateStatus(IStatus.ERROR, Messages.getString("KnoxInfoForm.check.configuration.knoxPassword")); //$NON-NLS-1$
            return false;
        }
        if (!validText(knoxDirectoryText.getText())) {
            updateStatus(IStatus.ERROR, Messages.getString("KnoxInfoForm.check.configuration.knoxDirectory")); //$NON-NLS-1$
            return false;
        }
        
        checkServicesBtn.setEnabled(true);
        updateStatus(IStatus.OK, null);
        return true;
    }
    
    @Override
    protected void addFieldsListeners() {
        
        knoxURLText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(final ModifyEvent e) {
                getConnection().getParameters().put(ConnParameterKeys.CONN_PARA_KEY_KNOX_URL,
                        knoxURLText.getText());
                checkFieldsValue();
            }
        });
        knoxUserText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(final ModifyEvent e) {
                getConnection().getParameters().put(ConnParameterKeys.CONN_PARA_KEY_KNOX_USER,
                        knoxUserText.getText());
                checkFieldsValue();
            }
        });
        knoxPasswordText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(final ModifyEvent e) {
                getConnection().getParameters().put(ConnParameterKeys.CONN_PARA_KEY_KNOX_PASSWORD,
                        knoxPasswordText.getText());
                checkFieldsValue();
            }
        });
        knoxDirectoryText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(final ModifyEvent e) {
                getConnection().getParameters().put(ConnParameterKeys.CONN_PARA_KEY_KNOX_DIRECTORY,
                        knoxDirectoryText.getText());
                checkFieldsValue();
            }
        });
        namenodePrincipalText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(final ModifyEvent e) {
                getConnection().setPrincipal(namenodePrincipalText.getText());
                checkFieldsValue();
            }
        });

        jtOrRmPrincipalText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(final ModifyEvent e) {
                getConnection().setJtOrRmPrincipal(jtOrRmPrincipalText.getText());
                checkFieldsValue();
            }
        });

        jobHistoryPrincipalText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                getConnection().setJobHistoryPrincipal(jobHistoryPrincipalText.getText());
                checkFieldsValue();
            }
        });

        kerberosBtn.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                hideControl(authNodejtOrRmHistoryComposite, !kerberosBtn.getSelection());
                hideControl(authKeytabComposite, !kerberosBtn.getSelection());
                getConnection().setEnableKerberos(kerberosBtn.getSelection());
                updateForm();
                authGroup.layout();
                authGroup.getParent().layout();
                checkFieldsValue();
            }
        });

        keytabBtn.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                getConnection().setUseKeytab(keytabBtn.getSelection());
                updateForm();
                checkFieldsValue();
            }
        });

        keytabPrincipalText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(final ModifyEvent e) {
                getConnection().setKeytabPrincipal(keytabPrincipalText.getText());
                checkFieldsValue();
            }
        });

        keytabText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(final ModifyEvent e) {
                getConnection().setKeytab(keytabText.getText());
                checkFieldsValue();
            }
        });
    }

    @Override
    public void updateForm() {
        kerberosBtn.setEnabled(true);
        namenodePrincipalText.setEditable(kerberosBtn.isEnabled() && kerberosBtn.getSelection());
        jtOrRmPrincipalText.setEditable(namenodePrincipalText.getEditable());
        jobHistoryPrincipalText.setEditable(getConnection().isEnableKerberos());
        keytabBtn.setEnabled(kerberosBtn.isEnabled() && kerberosBtn.getSelection());
        keytabPrincipalText.setEditable(keytabBtn.isEnabled() && keytabBtn.getSelection());
        keytabText.setEditable(keytabBtn.isEnabled() && keytabBtn.getSelection());
        keytabPrincipalText
                .setHideWidgets(!(kerberosBtn.isEnabled() && kerberosBtn.getSelection() && keytabBtn.isEnabled() && keytabBtn
                        .getSelection()));
        keytabText.setVisible(kerberosBtn.isEnabled() && kerberosBtn.getSelection() && keytabBtn.isEnabled()
                && keytabBtn.getSelection());
        hideKerberosControl(!kerberosBtn.getSelection());
        adaptFormToEditable();

        if (isContextMode()) {
            adaptFormToEditable();
        }
    }
    
    @Override
    protected void initialize() {
        init();
    }

    @Override
    public void init() {
        
        if (isNeedFillDefaults()) {
            fillDefaults();
        }
        
        if (isContextMode()) {
            adaptFormToEditable();
        }
        
        getConnection().getParameters().put(ConnParameterKeys.CONN_PARA_KEY_SPARK_MODE,"YARN_CLUSTER");
        
        String knoxURL = StringUtils.trimToEmpty(getConnection().getParameters().get(
                ConnParameterKeys.CONN_PARA_KEY_KNOX_URL));
        knoxURLText.setText(knoxURL);
        
        String knoxUser = StringUtils.trimToEmpty(getConnection().getParameters().get(
                ConnParameterKeys.CONN_PARA_KEY_KNOX_USER));
        knoxUserText.setText(knoxUser);
        
        String knoxPassword = StringUtils.trimToEmpty(getConnection().getParameters().get(
                ConnParameterKeys.CONN_PARA_KEY_KNOX_PASSWORD));
        knoxPasswordText.setText(knoxPassword);
        
        String knoxDirectory = StringUtils.trimToEmpty(getConnection().getParameters().get(
                ConnParameterKeys.CONN_PARA_KEY_KNOX_DIRECTORY));
        knoxDirectoryText.setText(knoxDirectory);
        
        updateStatus(IStatus.OK, EMPTY_STRING);
    }

    @Override
    protected void addFields() {
    	scrolledComposite = new ScrolledComposite(this, SWT.V_SCROLL | SWT.H_SCROLL);
        scrolledComposite.setExpandHorizontal(true);
        scrolledComposite.setExpandVertical(true);
        scrolledComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

        addConfigurationFields();
        addCheckFields();
        addAuthenticationFields();
    }
    
    private void fillDefaults() {
        HadoopClusterConnection connection = getConnection();
        if (creation && !connection.isUseCustomConfs()) {
            HCRepositoryUtil.fillDefaultValuesOfHadoopCluster(connection);
        }
    }
    
    @Override
    protected void collectConParameters() {
        collectConfigurationParameters(true);
    }

    private void collectConfigurationParameters(boolean isUse) {
        addContextParams(EHadoopParamName.KnoxUrl, isUse);
        addContextParams(EHadoopParamName.KnoxUsername, isUse);
        addContextParams(EHadoopParamName.KnoxPassword, isUse);
        addContextParams(EHadoopParamName.KnoxDirectory, isUse);
    }
    
    @Override
    protected void adaptFormToReadOnly() {
        readOnly = isReadOnly();
        knoxURLText.setReadOnly(readOnly);
        knoxUserText.setReadOnly(readOnly);
        knoxPasswordText.setReadOnly(readOnly);
        knoxDirectoryText.setReadOnly(readOnly);
        ((HadoopClusterForm) this.getParent()).adaptFormToReadOnly();
    }
    
    @Override
    protected void updateEditableStatus(boolean isEditable) {
        knoxURLText.setReadOnly(!isEditable);
        knoxUserText.setReadOnly(!isEditable);
        knoxPasswordText.setReadOnly(!isEditable);
        knoxDirectoryText.setReadOnly(!isEditable);
        ((HadoopClusterForm) this.getParent()).updateEditableStatus(isEditable);
    }
    
    @Override
    protected void adaptFormToEditable() {
        super.adaptFormToEditable();
        if (isContextMode()) {
            knoxPasswordText.getTextControl().setEchoChar('\0');
        } else {
            knoxPasswordText.getTextControl().setEchoChar('*');
        }
    }
    

}
