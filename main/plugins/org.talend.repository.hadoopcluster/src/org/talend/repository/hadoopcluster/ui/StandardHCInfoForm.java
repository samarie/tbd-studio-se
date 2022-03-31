// ============================================================================
//
// Copyright (C) 2006-2021 Talend Inc. - www.talend.com
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.talend.commons.exception.ExceptionHandler;
import org.talend.commons.ui.runtime.image.EImage;
import org.talend.commons.ui.runtime.image.ImageProvider;
import org.talend.commons.ui.swt.formtools.Form;
import org.talend.commons.ui.swt.formtools.LabelledCheckbox;
import org.talend.commons.ui.swt.formtools.LabelledCombo;
import org.talend.commons.ui.swt.formtools.LabelledFileField;
import org.talend.commons.ui.swt.formtools.LabelledText;
import org.talend.commons.ui.swt.formtools.LabelledWidget;
import org.talend.commons.ui.swt.formtools.UtilsButton;
import org.talend.core.database.conn.ConnParameterKeys;
import org.talend.core.hadoop.repository.HadoopRepositoryUtil;
import org.talend.core.hadoop.version.EAuthenticationMode;
import org.talend.core.hadoop.version.custom.ECustomVersionGroup;
import org.talend.core.model.components.ComponentCategory;
import org.talend.core.model.process.INode;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.utils.ContextParameterUtils;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.designer.core.IDesignerCoreService;
import org.talend.designer.core.model.utils.emf.talendfile.ContextType;
import org.talend.designer.hdfsbrowse.hadoop.service.EHadoopServiceType;
import org.talend.designer.hdfsbrowse.hadoop.service.HadoopServiceProperties;
import org.talend.designer.hdfsbrowse.hadoop.service.check.CheckHadoopServicesDialog;
import org.talend.designer.hdfsbrowse.manager.HadoopParameterValidator;
import org.talend.hadoop.distribution.DistributionFactory;
import org.talend.hadoop.distribution.component.MRComponent;
import org.talend.hadoop.distribution.constants.apache.ESparkMode;
import org.talend.hadoop.distribution.constants.apache.ISparkDistribution;
import org.talend.hadoop.distribution.constants.databricks.EDatabriksCloudProvider;
import org.talend.hadoop.distribution.constants.databricks.EDatabriksSubmitMode;
import org.talend.hadoop.distribution.helper.HadoopDistributionsHelper;
import org.talend.hadoop.distribution.model.DistributionBean;
import org.talend.hadoop.distribution.model.DistributionVersion;
import org.talend.metadata.managment.ui.dialog.HadoopPropertiesDialog;
import org.talend.metadata.managment.ui.dialog.SparkPropertiesDialog;
import org.talend.metadata.managment.ui.utils.ConnectionContextHelper;
import org.talend.metadata.managment.ui.utils.ExtendedNodeConnectionContextUtils.EHadoopParamName;
import org.talend.repository.hadoopcluster.conf.HadoopConfsUtils;
import org.talend.repository.hadoopcluster.i18n.Messages;
import org.talend.repository.hadoopcluster.ui.common.AbstractHadoopClusterInfoForm;
import org.talend.repository.hadoopcluster.ui.common.AbstractHadoopForm;
import org.talend.repository.hadoopcluster.ui.conf.HadoopContextConfConfigDialog;
import org.talend.repository.hadoopcluster.util.HCParameterUtil;
import org.talend.repository.hadoopcluster.util.HCRepositoryUtil;
import org.talend.repository.hadoopcluster.util.HCVersionUtil;
import org.talend.repository.model.hadoopcluster.HadoopClusterConnection;
import org.talend.repository.model.hadoopcluster.HadoopClusterConnectionItem;
import org.talend.repository.model.hadoopcluster.impl.HadoopClusterConnectionImpl;
import org.talend.repository.model.hadoopcluster.util.EncryptionUtil;

/**
 *
 * created by ycbai on 2014年9月16日 Detailled comment
 *
 */
public class StandardHCInfoForm extends AbstractHadoopClusterInfoForm<HadoopClusterConnection> {

    private SashForm sash;

    private Composite upsash;

    private Composite downsash;

    private ScrolledComposite scrolledComposite;

    private Composite bigComposite;

    private Composite parentForm;

    protected Composite propertiesComposite;

    private Composite hadoopPropertiesComposite;

    private Composite sparkPropertiesComposite;

    private SparkPropertiesDialog sparkPropertiesDialog;

    private Button useSparkPropertiesBtn;

    private LabelledCombo authenticationCombo;

    private Button kerberosBtn;

    private Composite authPartComposite;

    private Composite authCommonComposite;

    private Composite authNodejtOrRmHistoryComposite;

    private Composite authKeytabComposite;

    private Button keytabBtn;

    private Button useClouderaNaviBtn;

    private Button clouderaNaviButton;

    private LabelledText namenodeUriText;

    private LabelledText jobtrackerUriText;

    private LabelledText rmSchedulerText;

    private LabelledText jobHistoryText;

    private LabelledText stagingDirectoryText;

    private LabelledText namenodePrincipalText;

    private LabelledText jtOrRmPrincipalText;

    private LabelledText jobHistoryPrincipalText;

    private LabelledText keytabPrincipalText;

    private LabelledText userNameText;

    private LabelledText groupText;

    private Button useDNHostBtn;

    private Button hadoopConfsButton;

    private Button useCustomConfBtn;

    private Button setHadoopConfBtn;

    private Button browseHadoopConfBtn;

    private Text hadoopConfSpecificJarText;

    private Group hadoopConfsGroup;

    private ScrolledComposite propertiesScroll;

    private LabelledFileField keytabText;

    private Group customGroup;

    private HadoopPropertiesDialog propertiesDialog;

    private UtilsButton checkServicesBtn;

    private final boolean creation;

    protected DistributionBean hadoopDistribution;

    protected DistributionVersion hadoopVersison;

    private boolean needInitializeContext = false;

    // Mapr Ticket Authentication
    private Button maprTBtn;

    private LabelledText maprTPasswordText;

    private LabelledText maprTClusterText;

    private LabelledText maprTDurationText;

    private Button setMaprTHomeDirBtn;

    private Button setHadoopLoginBtn;

    private Button preloadAuthentificationBtn;

    private LabelledText maprTHomeDirText;

    private LabelledText maprTHadoopLoginText;

    private Composite authMaprTComposite;

    private Composite maprTPCDCompposite;

    private Composite maprTPasswordCompposite;

    private Composite maprTSetComposite;

    private Group authGroup;

    private Group connectionGroup;

    private Group webHDFSSSLEncryptionGrp;

    private Button useWebHDFSSSLEncryptionBtn;

    private Composite webHDFSSSLEncryptionDetailComposite;

    private LabelledFileField webHDFSSSLTrustStorePath;

    private LabelledText webHDFSSSLTrustStorePassword;

    private LabelledCombo sparkModeCombo;

    private ISparkDistribution sparkDistribution;

    private Group dataBricksGroup;

    private Group cdeGroup;

    private LabelledText endpointText;

    private LabelledCombo cloudProviderCombo;

    private LabelledCombo runSubmitCombo;

    private LabelledText clusterIDText;

    private LabelledText tokenText;

    private LabelledText dbfsDepFolderText;
    
    // CDE widgets
    private LabelledWidget cdeApiEndPoint;
    private LabelledWidget cdeAutoGenerateToken;
    private LabelledWidget cdeToken;
    private LabelledWidget cdeTokenEndpoint;
    private LabelledWidget cdeWorkloadUser;
    private LabelledWidget cdeWorkloadPassword;
    // Mapping between connection parameter and form field 
    private Map<String, LabelledWidget> fieldByParamKey;

    public StandardHCInfoForm(Composite parent, ConnectionItem connectionItem, String[] existingNames, boolean creation,
            DistributionBean hadoopDistribution, DistributionVersion hadoopVersison) {
        super(parent, SWT.NONE, existingNames);
        this.parentForm = parent;
        this.connectionItem = connectionItem;
        this.creation = creation;
        this.hadoopDistribution = hadoopDistribution;
        this.hadoopVersison = hadoopVersison;
        if (hadoopVersison != null && hadoopVersison.hadoopComponent instanceof ISparkDistribution) {
            this.sparkDistribution = (ISparkDistribution) hadoopVersison.hadoopComponent;
        }
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
    public void initialize() {
        // initialize for context mode
        if (needInitializeContext) {
            init();
        }
    }

    @Override
    public void init() {
        if (isNeedFillDefaults()) {
            fillDefaults();
        }
        if (isContextMode()) {
            adaptFormToEditable();
        }

        EAuthenticationMode authMode = EAuthenticationMode.getAuthenticationByName(getConnection().getAuthMode(), false);
        if (authMode != null) {
            authenticationCombo.setText(authMode.getDisplayName());
        } else {
            authenticationCombo.select(0);
        }
        HadoopClusterConnection connection = getConnection();
        namenodeUriText.setText(connection.getNameNodeURI());
        jobtrackerUriText.setText(connection.getJobTrackerURI());
        rmSchedulerText.setText(StringUtils.trimToEmpty(connection.getRmScheduler()));
        jobHistoryText.setText(StringUtils.trimToEmpty(connection.getJobHistory()));
        stagingDirectoryText.setText(StringUtils.trimToEmpty(connection.getStagingDirectory()));
        useDNHostBtn.setSelection(connection.isUseDNHost());
        useSparkPropertiesBtn.setSelection(connection.isUseSparkProperties());
        useCustomConfBtn.setSelection(connection.isUseCustomConfs());
        if (useClouderaNaviBtn != null) {
            useClouderaNaviBtn.setSelection(connection.isUseClouderaNavi());
        }
        kerberosBtn.setSelection(connection.isEnableKerberos());
        namenodePrincipalText.setText(connection.getPrincipal());
        jtOrRmPrincipalText.setText(connection.getJtOrRmPrincipal());
        jobHistoryPrincipalText.setText(connection.getJobHistoryPrincipal());
        keytabBtn.setSelection(connection.isUseKeytab());
        keytabPrincipalText.setText(connection.getKeytabPrincipal());
        keytabText.setText(connection.getKeytab());
        userNameText.setText(connection.getUserName());
        groupText.setText(connection.getGroup());

        //
        maprTBtn.setSelection(connection.isEnableMaprT());
        maprTPasswordText.setText(connection.getMaprTPassword());
        maprTClusterText.setText(connection.getMaprTCluster());
        maprTDurationText.setText(connection.getMaprTDuration());
        setMaprTHomeDirBtn.setSelection(connection.isSetMaprTHomeDir());
        setHadoopLoginBtn.setSelection(connection.isSetHadoopLogin());
        preloadAuthentificationBtn.setSelection(connection.isPreloadAuthentification());
        maprTHomeDirText.setText(connection.getMaprTHomeDir());
        maprTHadoopLoginText.setText(connection.getMaprTHadoopLogin());
        //
        useWebHDFSSSLEncryptionBtn.setSelection(connection.isUseWebHDFSSSL());
        webHDFSSSLTrustStorePath.setText(connection.getWebHDFSSSLTrustStorePath());
        webHDFSSSLTrustStorePassword.setText(connection.getWebHDFSSSLTrustStorePassword());

        setHadoopConfBtn.setSelection(
                Boolean.valueOf(HCParameterUtil.isOverrideHadoopConfs(connection)));
        hadoopConfSpecificJarText.setText(Optional
                .ofNullable(connection.getParameters().get(ConnParameterKeys.CONN_PARA_KEY_HADOOP_CONF_SPECIFIC_JAR))
                .orElse(""));

        needInitializeContext = true;
        updateStatus(IStatus.OK, EMPTY_STRING);

        onUseCustomConfBtnSelected(null);
        onOverrideHadoopConfBtnSelected(null);

        if ("SPARK".equals(((HadoopClusterConnectionImpl) this.connectionItem.getConnection()).getDistribution())) {
            useCustomConfBtn.setEnabled(false);
            useCustomConfBtn.setSelection(true);
            setHadoopConfBtn.setEnabled(false);
            setHadoopConfBtn.setSelection(true);
            hadoopConfSpecificJarText.setEditable(true);
            String sparkModeValue = getConnection().getParameters().get(ConnParameterKeys.CONN_PARA_KEY_SPARK_MODE);
            if (sparkModeValue != null) {
                sparkModeCombo.setText(getSparkModeByValue(sparkModeValue).getLabel());
            } else {
                sparkModeCombo.setText(ESparkMode.KUBERNETES.getLabel());
            }
            String providerValue = getConnection().getParameters().get(ConnParameterKeys.CONN_PARA_KEY_DATABRICKS_CLOUD_PROVIDER);
            if (providerValue != null) {
                cloudProviderCombo.setText(getDatabricksCloudProviderByValue(providerValue).getProviderLableName());
            } else {

                cloudProviderCombo.setText(EDatabriksCloudProvider.AWS.getProviderLableName());
            }
            String runModeValue = getConnection().getParameters().get(ConnParameterKeys.CONN_PARA_KEY_DATABRICKS_RUN_MODE);
            if (runModeValue != null) {
                runSubmitCombo.setText(getDatabricksRunModeByValue(runModeValue).getRunModeLabel());
            } else {
                runSubmitCombo.setText(EDatabriksSubmitMode.CREATE_RUN_JOB.getRunModeLabel());
            }

            String endPoint = StringUtils
                    .trimToEmpty(getConnection().getParameters().get(ConnParameterKeys.CONN_PARA_KEY_DATABRICKS_ENDPOINT));
            endpointText.setText(endPoint);

            String clusterId = StringUtils
                    .trimToEmpty(getConnection().getParameters().get(ConnParameterKeys.CONN_PARA_KEY_DATABRICKS_CLUSTER_ID));
            clusterIDText.setText(clusterId);

            String token = StringUtils
                    .trimToEmpty(EncryptionUtil.getValue(getConnection().getParameters().get(ConnParameterKeys.CONN_PARA_KEY_DATABRICKS_TOKEN), false));
            tokenText.setText(token);

            String folder = StringUtils
                    .trimToEmpty(getConnection().getParameters().get(ConnParameterKeys.CONN_PARA_KEY_DATABRICKS_DBFS_DEP_FOLDER));
            dbfsDepFolderText.setText(folder);
            
            // CDE - Set widget values from connection
            for (Entry<String, LabelledWidget> entry : fieldByParamKey.entrySet())
            {
                String value = StringUtils.trimToEmpty(getConnection().getParameters().get(entry.getKey()));
                entry.getValue().set(value);
            }
            updateCdeFieldsVisibility();
        }
    }

    @Override
    protected void adaptFormToReadOnly() {
        readOnly = isReadOnly();
        namenodeUriText.setReadOnly(readOnly);
        jobtrackerUriText.setReadOnly(readOnly);
        rmSchedulerText.setReadOnly(readOnly);
        jobHistoryText.setReadOnly(readOnly);
        stagingDirectoryText.setReadOnly(readOnly);
        useDNHostBtn.setEnabled(!readOnly);
        useSparkPropertiesBtn.setEnabled(!readOnly);
        sparkPropertiesDialog.propertyButton.setEnabled(!readOnly && useSparkPropertiesBtn.getSelection());
        useCustomConfBtn.setEnabled(!readOnly);
        hadoopConfsButton.setEnabled(!readOnly && useCustomConfBtn.getSelection() && !setHadoopConfBtn.getSelection());
        if (useClouderaNaviBtn != null) {
            useClouderaNaviBtn.setEnabled(!readOnly);
            clouderaNaviButton.setEnabled(!readOnly && useClouderaNaviBtn.getSelection());
        }
        kerberosBtn.setEnabled(!readOnly);
        namenodePrincipalText.setReadOnly(readOnly);
        jtOrRmPrincipalText.setReadOnly(readOnly);
        jobHistoryPrincipalText.setReadOnly(readOnly);
        userNameText.setReadOnly(readOnly);
        groupText.setReadOnly(readOnly);
        maprTBtn.setEnabled(!readOnly);
        maprTPasswordText.setReadOnly(readOnly);
        maprTClusterText.setReadOnly(readOnly);
        maprTDurationText.setReadOnly(readOnly);
        setMaprTHomeDirBtn.setEnabled(!readOnly);
        setHadoopLoginBtn.setEnabled(!readOnly);
        preloadAuthentificationBtn.setEnabled(!readOnly);
        maprTHomeDirText.setReadOnly(readOnly);
        maprTHadoopLoginText.setReadOnly(readOnly);
        // setHadoopConfBtn.setEnabled(!readOnly);
        hadoopConfSpecificJarText.setEditable(!readOnly && setHadoopConfBtn.getSelection());
        browseHadoopConfBtn.setEnabled(!readOnly && setHadoopConfBtn.getSelection());

        useWebHDFSSSLEncryptionBtn.setEnabled(!readOnly);
        webHDFSSSLTrustStorePath.setReadOnly(readOnly);
        webHDFSSSLTrustStorePassword.setReadOnly(readOnly);
        if ("SPARK".equals(((HadoopClusterConnectionImpl) this.connectionItem.getConnection()).getDistribution())) {
            useCustomConfBtn.setEnabled(false);
            useCustomConfBtn.setSelection(true);
            setHadoopConfBtn.setEnabled(false);
            setHadoopConfBtn.setSelection(true);
            hadoopConfSpecificJarText.setEditable(true);
            sparkModeCombo.setEnabled(!readOnly);
            runSubmitCombo.setEnabled(!readOnly);
            cloudProviderCombo.setEnabled(!readOnly);
            endpointText.setEnabled(!readOnly);
            clusterIDText.setEnabled(!readOnly);
            tokenText.setEnabled(!readOnly);
            dbfsDepFolderText.setEnabled(!readOnly);
        }
    }

    @Override
    protected void adaptFormToEditable() {
        super.adaptFormToEditable();
        if (isContextMode()) {
            maprTPasswordText.getTextControl().setEchoChar('\0');
            webHDFSSSLTrustStorePassword.getTextControl().setEchoChar('\0');
        } else {
            maprTPasswordText.getTextControl().setEchoChar('*');
            webHDFSSSLTrustStorePassword.getTextControl().setEchoChar('*');
        }
    }

    @Override
    protected void updateEditableStatus(boolean isEditable) {
        authenticationCombo.setEnabled(isEditable);
        namenodeUriText.setEditable(isEditable);
        jobtrackerUriText.setEditable(isEditable);
        rmSchedulerText.setEditable(isEditable);
        jobHistoryText.setEditable(isEditable);
        stagingDirectoryText.setEditable(isEditable);
        useDNHostBtn.setEnabled(isEditable);
        kerberosBtn.setEnabled(isEditable && (isCurrentHadoopVersionSupportSecurity() || isCustomUnsupportHasSecurity()));
        boolean isKerberosEditable = kerberosBtn.isEnabled() && kerberosBtn.getSelection();
        namenodePrincipalText.setEditable(isKerberosEditable);
        jtOrRmPrincipalText.setEditable(isKerberosEditable);
        jobHistoryPrincipalText.setEditable(isEditable && isJobHistoryPrincipalEditable());
        userNameText.setEditable(isEditable && !kerberosBtn.getSelection());
        groupText.setEditable(isEditable && (isCurrentHadoopVersionSupportGroup() || isCustomUnsupportHasGroup()));
        keytabBtn.setEnabled(isEditable && kerberosBtn.getSelection());
        boolean isKeyTabEditable = keytabBtn.isEnabled() && keytabBtn.getSelection();
        keytabText.setEditable(isKeyTabEditable);
        keytabPrincipalText.setEditable(isKeyTabEditable);
        maprTBtn.setEnabled(isEditable && isCurrentHadoopVersionSupportMapRTicket());
        boolean isMaprTEditable = maprTBtn.isEnabled() && maprTBtn.getSelection();
        maprTPasswordText.setEditable(isMaprTEditable && !isKerberosEditable);
        maprTClusterText.setEditable(isMaprTEditable);
        maprTDurationText.setEditable(isMaprTEditable);
        setMaprTHomeDirBtn.setEnabled(isEditable && maprTBtn.getSelection());
        setHadoopLoginBtn.setEnabled(isEditable && maprTBtn.getSelection());
        preloadAuthentificationBtn.setEnabled(isEditable && maprTBtn.getSelection());
        maprTHomeDirText.setEditable(isMaprTEditable);
        maprTHadoopLoginText.setEditable(isMaprTEditable);
        useWebHDFSSSLEncryptionBtn.setEnabled(isEditable && isCurrentHadoopVersionSupportWebHDFS());
        boolean isUseWebHDFSSSLEncryptionBtnEditable = useWebHDFSSSLEncryptionBtn.isEnabled()
                && useWebHDFSSSLEncryptionBtn.getSelection();
        webHDFSSSLTrustStorePath.setEditable(isUseWebHDFSSSLEncryptionBtnEditable);
        webHDFSSSLTrustStorePassword.setEditable(isUseWebHDFSSSLEncryptionBtnEditable);

        propertiesDialog.updateStatusLabel(getHadoopProperties());
        useSparkPropertiesBtn.setEnabled(isEditable);
        sparkPropertiesDialog.updateStatusLabel(getSparkProperties());
        ((HadoopClusterForm) this.getParent()).updateEditableStatus(isEditable);

        // setHadoopConfBtn.setEnabled(isEditable);
        hadoopConfSpecificJarText.setEditable(isEditable && setHadoopConfBtn.getSelection());
        browseHadoopConfBtn.setEnabled(isEditable && setHadoopConfBtn.getSelection());
        sparkModeCombo.setEnabled(isEditable);
        runSubmitCombo.setEnabled(isEditable);
        cloudProviderCombo.setEnabled(isEditable);
        endpointText.setEnabled(isEditable);
        clusterIDText.setEnabled(isEditable);
        tokenText.setEnabled(isEditable);
        dbfsDepFolderText.setEnabled(isEditable);
    }

    @Override
    protected void addFields() {
        if ("SPARK".equals(((HadoopClusterConnectionImpl) this.connectionItem.getConnection()).getDistribution())) { //$NON-NLS-1$
            Group configGroup = Form.createGroup(this, 2, Messages.getString("HadoopClusterForm.sparkConfig"), 120); //$NON-NLS-1$
            configGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            sparkModeCombo = new LabelledCombo(configGroup, Messages.getString("HadoopClusterForm.sparkMode"), //$NON-NLS-1$
                    Messages.getString("HadoopClusterForm.sparkMode.tooltip"), // $NON-NLS-2$
                    getSparkModes());
            String sparkModeValue = getConnection().getParameters().get(ConnParameterKeys.CONN_PARA_KEY_SPARK_MODE);
            if (sparkModeValue != null) {
                sparkModeCombo.setText(getSparkModeByValue(sparkModeValue).getLabel());
            } else {
                sparkModeCombo.setText(ESparkMode.KUBERNETES.getLabel());
            }
            getConnection().getParameters().put(ConnParameterKeys.CONN_PARA_KEY_SPARK_MODE,
                    getSparkModeByName(sparkModeCombo.getText()).getValue());
        }

        Composite parent = new Composite(this, SWT.NONE);
        parent.setLayout(new FillLayout());
        GridData parentGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        parent.setLayoutData(parentGridData);

        sash = new SashForm(parent, SWT.VERTICAL | SWT.SMOOTH);
        sash.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        GridLayout layout = new GridLayout();
        sash.setLayout(layout);
        upsash = new Composite(sash, SWT.NONE);
        GridLayout upsashLayout = new GridLayout(1, false);
        upsash.setLayout(upsashLayout);

        downsash = new Composite(sash, SWT.NONE);
        GridLayout downsashLayout = new GridLayout(1, false);
        downsash.setLayout(downsashLayout);
        sash.setWeights(new int[] { 21, 17 });

        scrolledComposite = new ScrolledComposite(upsash, SWT.V_SCROLL | SWT.H_SCROLL);
        scrolledComposite.setExpandHorizontal(true);
        scrolledComposite.setExpandVertical(true);
        scrolledComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

        bigComposite = Form.startNewGridLayout(scrolledComposite, 1);
        scrolledComposite.setContent(bigComposite);

        addCustomFields();
        addConnectionFields(bigComposite);
        addWebHDFSEncryptionFields(bigComposite);
        addAuthenticationFields(bigComposite);
        addDatabricksFields();
        addCdeFields();

        propertiesScroll = new ScrolledComposite(downsash, SWT.V_SCROLL | SWT.H_SCROLL);
        propertiesScroll.setExpandHorizontal(true);
        propertiesScroll.setExpandVertical(true);
        propertiesScroll.setLayoutData(new GridData(GridData.FILL_BOTH));

        propertiesComposite = Form.startNewGridLayout(propertiesScroll, 1);
        propertiesScroll.setContent(propertiesComposite);
        GridLayout propertiesLayout = new GridLayout(2, false);
        propertiesLayout.marginWidth = 0;
        propertiesLayout.marginHeight = 0;
        propertiesComposite.setLayout(propertiesLayout);
        propertiesComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        addHadoopPropertiesFields();
        addSparkPropertiesFields();
        addNavigatorFields();
        addHadoopConfsFields();

        addCheckFields();

        hideFieldsOnSparkMode();

        scrolledComposite.addControlListener(new ControlAdapter() {

            @Override
            public void controlResized(ControlEvent e) {
                Rectangle r = scrolledComposite.getClientArea();
                // scrolledComposite.setMinSize(bigComposite.computeSize(r.width-100, 550));
                if (Platform.getOS().equals(Platform.OS_LINUX)) {
                    scrolledComposite.setMinSize(bigComposite.computeSize(SWT.DEFAULT, 900));
                } else {
                    scrolledComposite.setMinSize(bigComposite.computeSize(SWT.DEFAULT, 620));
                }
            }
        });
        propertiesScroll.addControlListener(new ControlAdapter() {

            @Override
            public void controlResized(ControlEvent e) {
                propertiesScroll.setMinSize(propertiesComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
            }
        });
    }

    private void addDatabricksFields() {
        dataBricksGroup = Form.createGroup(bigComposite, 2, Messages.getString("DataBricksInfoForm.text.configuration"), 110); //$NON-NLS-1$
        dataBricksGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        runSubmitCombo = new LabelledCombo(dataBricksGroup, Messages.getString("DataBricksInfoForm.text.runSubmitMode"), "", //$NON-NLS-1$ $NON-NLS-2$
                getRunSubmitModes());
        cloudProviderCombo = new LabelledCombo(dataBricksGroup, Messages.getString("DataBricksInfoForm.text.cloudProvider"), "", //$NON-NLS-1$ $NON-NLS-2$
                getProviders());
        endpointText = new LabelledText(dataBricksGroup, Messages.getString("DataBricksInfoForm.text.endPoint"), 1); //$NON-NLS-1$

        clusterIDText = new LabelledText(dataBricksGroup, Messages.getString("DataBricksInfoForm.text.clusterID"), 1); //$NON-NLS-1$

        tokenText = new LabelledText(dataBricksGroup, Messages.getString("DataBricksInfoForm.text.token"), 1, //$NON-NLS-1$
                SWT.PASSWORD | SWT.BORDER | SWT.SINGLE);

        dbfsDepFolderText = new LabelledText(dataBricksGroup, Messages.getString("DataBricksInfoForm.text.dbfsDepFolder"), 1); //$NON-NLS-1$
    }

    private void addCdeFields() {
        cdeGroup = Form.createGroup(bigComposite, 2, Messages.getString("CdeInfoForm.text.configuration"), 110); //$NON-NLS-1$
        cdeGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        cdeApiEndPoint = new LabelledText(cdeGroup,  Messages.getString("CdeInfoForm.text.cdeApiEndPoint"));  //$NON-NLS-1$
        cdeAutoGenerateToken = new LabelledCheckbox(cdeGroup, Messages.getString("CdeInfoForm.checkbox.cdeAutoGenerateToken")); //$NON-NLS-1$
        cdeToken = new LabelledText(cdeGroup,  Messages.getString("CdeInfoForm.text.cdeToken"), 1, SWT.PASSWORD | SWT.BORDER | SWT.SINGLE);  //$NON-NLS-1$
        cdeTokenEndpoint = new LabelledText(cdeGroup,  Messages.getString("CdeInfoForm.text.cdeTokenEndpoint"));  //$NON-NLS-1$
        cdeWorkloadUser = new LabelledText(cdeGroup,  Messages.getString("CdeInfoForm.text.cdeWorkloadUser"));  //$NON-NLS-1$
        cdeWorkloadPassword = new LabelledText(cdeGroup,  Messages.getString("CdeInfoForm.text.cdeWorkloadPassword"), 1, SWT.PASSWORD | SWT.BORDER | SWT.SINGLE);  //$NON-NLS-1$

        fieldByParamKey = new HashMap<String, LabelledWidget>();
        fieldByParamKey.put(ConnParameterKeys.CONN_PARA_KEY_CDE_API_ENDPOINT, cdeApiEndPoint);
        fieldByParamKey.put(ConnParameterKeys.CONN_PARA_KEY_CDE_TOKEN, cdeToken);
        fieldByParamKey.put(ConnParameterKeys.CONN_PARA_KEY_CDE_AUTO_GENERATE_TOKEN, cdeAutoGenerateToken);
        fieldByParamKey.put(ConnParameterKeys.CONN_PARA_KEY_CDE_TOKEN_ENDPOINT, cdeTokenEndpoint);
        fieldByParamKey.put(ConnParameterKeys.CONN_PARA_KEY_CDE_WORKLOAD_USER, cdeWorkloadUser);
        fieldByParamKey.put(ConnParameterKeys.CONN_PARA_KEY_CDE_WORKLOAD_PASSWORD, cdeWorkloadPassword);
    }

    private List<String> getRunSubmitModes() {
        List<String> runSubmitLabelNames = new ArrayList<String>();
        if (sparkDistribution != null) {
            List<EDatabriksSubmitMode> runSubmitModes = sparkDistribution.getRunSubmitMode();
            if (runSubmitModes != null) {
                runSubmitLabelNames = runSubmitModes.stream().map(mode -> {
                    return mode.getRunModeLabel();
                }).collect(Collectors.toList());
            }
        }
        return runSubmitLabelNames;
    }

    private List<String> getProviders() {
        List<String> providerLableNames = new ArrayList<String>();
        if (sparkDistribution != null) {
            List<EDatabriksCloudProvider> supportCloudProviders = sparkDistribution.getSupportCloudProviders();
            if (supportCloudProviders != null) {
                providerLableNames = supportCloudProviders.stream().map(provider -> {
                    return provider.getProviderLableName();
                }).collect(Collectors.toList());
            }
        }
        return providerLableNames;
    }

    private void addCustomFields() {
        customGroup = Form.createGroup(this, 4, Messages.getString("HadoopClusterForm.customSettings")); //$NON-NLS-1$
        customGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        authenticationCombo = new LabelledCombo(customGroup,
                Messages.getString("HadoopClusterForm.authentication"), //$NON-NLS-1$
                Messages.getString("HadoopClusterForm.authentication.tooltip"), EAuthenticationMode.getAllAuthenticationDisplayNames() //$NON-NLS-1$
                        .toArray(new String[0]), 1, false);
    }

    private void addConnectionFields(Composite scrolledComposite) {
        connectionGroup = Form.createGroup(scrolledComposite, 1, Messages.getString("HadoopClusterForm.connectionSettings"), //$NON-NLS-1$
                110);
        connectionGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        Composite uriPartComposite = new Composite(connectionGroup, SWT.NULL);
        GridLayout uriPartLayout = new GridLayout(2, false);
        uriPartLayout.marginWidth = 0;
        uriPartLayout.marginHeight = 0;
        uriPartComposite.setLayout(uriPartLayout);
        uriPartComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        namenodeUriText = new LabelledText(uriPartComposite, Messages.getString("HadoopClusterForm.text.namenodeURI"), 1); //$NON-NLS-1$
        jobtrackerUriText = new LabelledText(uriPartComposite, Messages.getString("HadoopClusterForm.text.jobtrackerURI"), 1); //$NON-NLS-1$
        rmSchedulerText = new LabelledText(uriPartComposite, Messages.getString("HadoopClusterForm.text.rmScheduler"), 1); //$NON-NLS-1$
        jobHistoryText = new LabelledText(uriPartComposite, Messages.getString("HadoopClusterForm.text.jobHistory"), 1); //$NON-NLS-1$
        stagingDirectoryText = new LabelledText(uriPartComposite,
                Messages.getString("HadoopClusterForm.text.stagingDirectory"), 1); //$NON-NLS-1$
        useDNHostBtn = new Button(uriPartComposite, SWT.CHECK);
        useDNHostBtn.setText(Messages.getString("HadoopClusterForm.button.useDNHost")); //$NON-NLS-1$
        useDNHostBtn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));
    }

    private void addWebHDFSEncryptionFields(Composite downsash) {
        webHDFSSSLEncryptionGrp = Form.createGroup(downsash, 1, Messages.getString("HadoopClusterForm.webHDFS.encryption"), 110); //$NON-NLS-1$
        webHDFSSSLEncryptionGrp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        useWebHDFSSSLEncryptionBtn = new Button(webHDFSSSLEncryptionGrp, SWT.CHECK);
        useWebHDFSSSLEncryptionBtn.setText(Messages.getString("HadoopClusterForm.webHDFS.encryption.useSSLEncryption")); //$NON-NLS-1$
        useWebHDFSSSLEncryptionBtn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 5, 1));

        webHDFSSSLEncryptionDetailComposite = new Composite(webHDFSSSLEncryptionGrp, SWT.NULL);
        GridLayout webHDFSSSLEncryptionDetailCompLayout = new GridLayout(5, false);
        webHDFSSSLEncryptionDetailCompLayout.marginWidth = 0;
        webHDFSSSLEncryptionDetailCompLayout.marginHeight = 0;
        webHDFSSSLEncryptionDetailComposite.setLayout(webHDFSSSLEncryptionDetailCompLayout);
        webHDFSSSLEncryptionDetailComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        String[] extensions = { "*.*" }; //$NON-NLS-1$
        webHDFSSSLTrustStorePath = new LabelledFileField(webHDFSSSLEncryptionDetailComposite,
                Messages.getString("HadoopClusterForm.webHDFS.encryption.useSSLEncryption.trustStorePath"), extensions); //$NON-NLS-1$
        webHDFSSSLTrustStorePassword = new LabelledText(webHDFSSSLEncryptionDetailComposite,
                Messages.getString("HadoopClusterForm.webHDFS.encryption.useSSLEncryption.trustStorePassword"), 1, SWT.PASSWORD | SWT.BORDER | SWT.SINGLE); //$NON-NLS-1$        
        webHDFSSSLTrustStorePassword.getTextControl().setEchoChar('*');
    }

    private void addAuthenticationFields(Composite downsash) {
        authGroup = Form.createGroup(downsash, 1, Messages.getString("HadoopClusterForm.authenticationSettings"), 110); //$NON-NLS-1$
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

        // placeHolder is only used to make userNameText and groupText to new line
        Composite placeHolder = new Composite(authCommonComposite, SWT.NULL);
        GridLayout placeHolderLayout = new GridLayout(4, false);
        placeHolderLayout.marginWidth = 0;
        placeHolderLayout.marginHeight = 0;
        placeHolder.setLayout(placeHolderLayout);
        placeHolder.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        userNameText = new LabelledText(placeHolder, Messages.getString("HadoopClusterForm.text.userName"), 1); //$NON-NLS-1$
        groupText = new LabelledText(placeHolder, Messages.getString("HadoopClusterForm.text.group"), 1); //$NON-NLS-1$

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

        // Mapr Ticket Authentication
        authMaprTComposite = new Composite(authPartComposite, SWT.NULL);
        GridLayout authMaprTicketCompLayout = new GridLayout(1, false);
        authMaprTicketCompLayout.marginWidth = 0;
        authMaprTicketCompLayout.marginHeight = 0;
        authMaprTComposite.setLayout(authMaprTicketCompLayout);
        authMaprTComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        maprTBtn = new Button(authMaprTComposite, SWT.CHECK);
        maprTBtn.setText(Messages.getString("HadoopClusterForm.button.maprTicket")); //$NON-NLS-1$
        maprTBtn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));

        maprTPCDCompposite = new Composite(authMaprTComposite, SWT.NULL);
        GridLayout maprTPCDCompositeLayout = new GridLayout(1, false);
        maprTPCDCompositeLayout.marginWidth = 0;
        maprTPCDCompositeLayout.marginHeight = 0;
        maprTPCDCompposite.setLayout(maprTPCDCompositeLayout);
        maprTPCDCompposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        maprTPasswordCompposite = new Composite(maprTPCDCompposite, SWT.NULL);
        GridLayout maprTPasswordComppositeLayout = new GridLayout(2, false);
        maprTPasswordComppositeLayout.marginWidth = 0;
        maprTPasswordComppositeLayout.marginHeight = 0;
        maprTPasswordCompposite.setLayout(maprTPasswordComppositeLayout);
        maprTPasswordCompposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        maprTPasswordText = new LabelledText(maprTPasswordCompposite,
                Messages.getString("HadoopClusterForm.text.maprTPassword"), 1, SWT.PASSWORD | SWT.BORDER | SWT.SINGLE); //$NON-NLS-1$
        maprTPasswordText.getTextControl().setEchoChar('*');

        Composite maprTCDCompposite = new Composite(maprTPCDCompposite, SWT.NULL);
        GridLayout maprTCDComppositeLayout = new GridLayout(2, false);
        maprTCDComppositeLayout.marginWidth = 0;
        maprTCDComppositeLayout.marginHeight = 0;
        maprTCDCompposite.setLayout(maprTCDComppositeLayout);
        maprTCDCompposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        maprTClusterText = new LabelledText(maprTCDCompposite, Messages.getString("HadoopClusterForm.text.maprTCluster"), 1); //$NON-NLS-1$
        maprTDurationText = new LabelledText(maprTCDCompposite, Messages.getString("HadoopClusterForm.text.maprTDuration"), 1); //$NON-NLS-1$

        maprTSetComposite = new Composite(authMaprTComposite, SWT.NULL);
        GridLayout maprTicketSetCompLayout = new GridLayout(3, false);
        maprTicketSetCompLayout.marginWidth = 0;
        maprTicketSetCompLayout.marginHeight = 0;
        maprTSetComposite.setLayout(maprTicketSetCompLayout);
        maprTSetComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        setMaprTHomeDirBtn = new Button(maprTSetComposite, SWT.CHECK);
        setMaprTHomeDirBtn.setText(Messages.getString("HadoopClusterForm.button.setMaprTHomeDir")); //$NON-NLS-1$
        maprTHomeDirText = new LabelledText(maprTSetComposite, "", 1); //$NON-NLS-1$

        setHadoopLoginBtn = new Button(maprTSetComposite, SWT.CHECK);
        setHadoopLoginBtn.setText(Messages.getString("HadoopClusterForm.button.setHadoopLogin")); //$NON-NLS-1$
        maprTHadoopLoginText = new LabelledText(maprTSetComposite, "", 1); //$NON-NLS-1$

        preloadAuthentificationBtn = new Button(maprTSetComposite, SWT.CHECK);
        preloadAuthentificationBtn.setText(Messages.getString("HadoopClusterForm.button.preloadAuthentification.label")); //$NON-NLS-1$
    }

    private void addHadoopPropertiesFields() {
        hadoopPropertiesComposite = new Composite(propertiesComposite, SWT.NONE);
        GridLayout hadoopPropertiesLayout = new GridLayout(1, false);
        hadoopPropertiesLayout.marginWidth = 0;
        hadoopPropertiesLayout.marginHeight = 0;
        hadoopPropertiesComposite.setLayout(hadoopPropertiesLayout);
        hadoopPropertiesComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        propertiesDialog = new HadoopPropertiesDialog(getShell(), getHadoopProperties()) {

            @Override
            protected boolean isReadOnly() {
                return !isEditable();
            }

            @Override
            protected List<Map<String, Object>> getLatestInitProperties() {
                return getHadoopProperties();
            }

            @Override
            public void applyProperties(List<Map<String, Object>> properties) {
                getConnection().setHadoopProperties(HadoopRepositoryUtil.getHadoopPropertiesJsonStr(properties));
            }

        };
        propertiesDialog.createPropertiesFields(hadoopPropertiesComposite);
    }

    private List<Map<String, Object>> getHadoopProperties() {
        String hadoopProperties = getConnection().getHadoopProperties();
        List<Map<String, Object>> hadoopPropertiesList = HadoopRepositoryUtil.getHadoopPropertiesList(hadoopProperties);
        return hadoopPropertiesList;
    }

    protected void addSparkPropertiesFields() {
        sparkPropertiesComposite = new Composite(propertiesComposite, SWT.NONE);
        GridLayout sparkPropertiesLayout = new GridLayout(3, false);
        sparkPropertiesLayout.marginWidth = 5;
        sparkPropertiesLayout.marginHeight = 5;
        sparkPropertiesComposite.setLayout(sparkPropertiesLayout);
        sparkPropertiesComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        useSparkPropertiesBtn = new Button(sparkPropertiesComposite, SWT.CHECK);
        useSparkPropertiesBtn.setText(Messages.getString("HadoopClusterForm.button.useSparkProperties")); //$NON-NLS-1$
        useSparkPropertiesBtn.setLayoutData(new GridData());

        sparkPropertiesDialog = new SparkPropertiesDialog(getShell(), getSparkProperties()) {

            @Override
            protected boolean isReadOnly() {
                return !(useSparkPropertiesBtn.getSelection() && isEditable());
            }

            @Override
            protected List<Map<String, Object>> getLatestInitProperties() {
                return getSparkProperties();
            }

            @Override
            public void applyProperties(List<Map<String, Object>> properties) {
                getConnection().setSparkProperties(HadoopRepositoryUtil.getHadoopPropertiesJsonStr(properties));
            }

        };
        sparkPropertiesDialog.createPropertiesFields(sparkPropertiesComposite);
    }

    private List<Map<String, Object>> getSparkProperties() {
        String sparkProperties = getConnection().getSparkProperties();
        List<Map<String, Object>> sparkPropertiesList = HadoopRepositoryUtil.getHadoopPropertiesList(sparkProperties);
        return sparkPropertiesList;
    }

    private void addNavigatorFields() {
        DistributionBean distriBean = getDistribution();
        MRComponent currentDistribution;
        boolean isShow = false;
        try {
            currentDistribution = (MRComponent) DistributionFactory.buildDistribution(distriBean.getName(),
                    hadoopVersison.getVersion());
            isShow = !getDistribution().useCustom() && currentDistribution.doSupportClouderaNavigator();
        } catch (Exception e) {
            ExceptionHandler.process(e);
        }
        if (!isShow) {
            return;
        }

        Composite clouderaNaviComposite = new Composite(propertiesComposite, SWT.NONE);
        GridLayout hadoopConfsCompLayout = new GridLayout(3, false);
        hadoopConfsCompLayout.marginWidth = 5;
        hadoopConfsCompLayout.marginHeight = 5;
        clouderaNaviComposite.setLayout(hadoopConfsCompLayout);
        clouderaNaviComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        useClouderaNaviBtn = new Button(clouderaNaviComposite, SWT.CHECK);
        useClouderaNaviBtn.setText(Messages.getString("HadoopClusterForm.button.use_cloudera_navigator")); //$NON-NLS-1$
        useClouderaNaviBtn.setLayoutData(new GridData());

        clouderaNaviButton = new Button(clouderaNaviComposite, SWT.NONE);
        clouderaNaviButton.setImage(ImageProvider.getImage(EImage.THREE_DOTS_ICON));
        clouderaNaviButton.setLayoutData(new GridData(30, 25));
        clouderaNaviButton.setEnabled(false);

        Label displayLabel = new Label(clouderaNaviComposite, SWT.NONE);
        displayLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    }

    private void addHadoopConfsFields() {
        Composite hadoopConfsComposite = new Composite(propertiesComposite, SWT.NONE);
        GridLayout hadoopConfsCompLayout = new GridLayout(1, false);
        hadoopConfsCompLayout.marginWidth = 5;
        hadoopConfsCompLayout.marginHeight = 5;
        hadoopConfsComposite.setLayout(hadoopConfsCompLayout);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 2;
        hadoopConfsComposite.setLayoutData(gridData);

        useCustomConfBtn = new Button(hadoopConfsComposite, SWT.CHECK);
        useCustomConfBtn.setText(Messages.getString("HadoopClusterForm.button.useCustomConf.label")); //$NON-NLS-1$
        useCustomConfBtn.setLayoutData(new GridData());

        hadoopConfsGroup = new Group(hadoopConfsComposite, SWT.NONE);
        hadoopConfsGroup.setText(Messages.getString("HadoopClusterForm.group.customConf")); //$NON-NLS-1$
        FormLayout hadoopConfsGroupLayout = new FormLayout();
        hadoopConfsGroupLayout.marginHeight = 5;
        hadoopConfsGroupLayout.marginWidth = 5;
        hadoopConfsGroup.setLayout(hadoopConfsGroupLayout);
        GridData fieldsGridData = new GridData(GridData.FILL_HORIZONTAL);
        fieldsGridData.horizontalSpan = 2;
        hadoopConfsGroup.setLayoutData(fieldsGridData);

        hadoopConfsButton = new Button(hadoopConfsGroup, SWT.NONE);
        hadoopConfsButton.setText(Messages.getString("HadoopClusterForm.button.config")); //$NON-NLS-1$
        hadoopConfsButton.setEnabled(false);
        FormData formData = new FormData();
        formData.left = new FormAttachment(0);
        formData.top = new FormAttachment(0);
        hadoopConfsButton.setLayoutData(formData);

        setHadoopConfBtn = new Button(hadoopConfsGroup, SWT.CHECK);
        setHadoopConfBtn.setText(Messages.getString("HadoopClusterForm.button.overrideCustomConf")); //$NON-NLS-1$
        formData = new FormData();
        formData.left = new FormAttachment(hadoopConfsButton, 0, SWT.LEFT);
        formData.top = new FormAttachment(hadoopConfsButton, 5, SWT.BOTTOM);
        setHadoopConfBtn.setLayoutData(formData);

        browseHadoopConfBtn = new Button(hadoopConfsGroup, SWT.NONE);
        browseHadoopConfBtn.setText("...");
        browseHadoopConfBtn.setToolTipText(Messages.getString("HadoopClusterForm.button.overrideCustomConfPath.browse")); //$NON-NLS-1$
        formData = new FormData();
        formData.top = new FormAttachment(setHadoopConfBtn, 0, SWT.CENTER);
        formData.right = new FormAttachment(100);
        browseHadoopConfBtn.setLayoutData(formData);

        hadoopConfSpecificJarText = new Text(hadoopConfsGroup, SWT.BORDER);
        formData = new FormData();
        formData.left = new FormAttachment(setHadoopConfBtn, 5, SWT.RIGHT);
        formData.top = new FormAttachment(setHadoopConfBtn, 0, SWT.CENTER);
        formData.right = new FormAttachment(browseHadoopConfBtn, -5, SWT.LEFT);
        hadoopConfSpecificJarText.setLayoutData(formData);
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
    protected void addFieldsListeners() {
        authenticationCombo.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(final ModifyEvent e) {
                String newAuthDisplayName = authenticationCombo.getText();
                EAuthenticationMode newAuthMode = EAuthenticationMode.getAuthenticationByDisplayName(newAuthDisplayName);
                String originalAuthName = getConnection().getAuthMode();
                EAuthenticationMode originalAuthMode = EAuthenticationMode.getAuthenticationByName(originalAuthName, false);
                if (newAuthMode != null && newAuthMode != originalAuthMode) {
                    if (EAuthenticationMode.UGI.equals(newAuthMode)) {
                        maprTBtn.setEnabled(true);
                        hideControl(maprTBtn, false);
                        if (maprTBtn.getSelection()) {
                            hideControl(maprTPCDCompposite, false);
                            hideControl(maprTSetComposite, false);
                        }
                        authMaprTComposite.layout();
                        authMaprTComposite.getParent().layout();
                    }
                    getConnection().setAuthMode(newAuthMode.getName());
                    updateForm();
                    checkFieldsValue();
                }
            }
        });

        namenodeUriText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(final ModifyEvent e) {
                getConnection().setNameNodeURI(namenodeUriText.getText());
                checkFieldsValue();
            }
        });

        jobtrackerUriText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(final ModifyEvent e) {
                getConnection().setJobTrackerURI(jobtrackerUriText.getText());
                checkFieldsValue();
            }
        });

        rmSchedulerText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(final ModifyEvent e) {
                getConnection().setRmScheduler(rmSchedulerText.getText());
                checkFieldsValue();
            }
        });

        jobHistoryText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(final ModifyEvent e) {
                getConnection().setJobHistory(jobHistoryText.getText());
                checkFieldsValue();
            }
        });

        stagingDirectoryText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(final ModifyEvent e) {
                getConnection().setStagingDirectory(stagingDirectoryText.getText());
                checkFieldsValue();
            }
        });

        useDNHostBtn.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                getConnection().setUseDNHost(useDNHostBtn.getSelection());
                checkFieldsValue();
            }
        });

        useSparkPropertiesBtn.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                sparkPropertiesDialog.propertyButton.setEnabled(useSparkPropertiesBtn.getSelection());
                getConnection().setUseSparkProperties(useSparkPropertiesBtn.getSelection());
                checkFieldsValue();
            }
        });

        useCustomConfBtn.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                onUseCustomConfBtnSelected(e);
            }

        });

        hadoopConfsButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                AbstractHadoopForm form = null;
                if (parentForm instanceof AbstractHadoopForm) {
                    form = (AbstractHadoopForm) parentForm;
                }
                new HadoopContextConfConfigDialog(getShell(), form, (HadoopClusterConnectionItem) connectionItem).open();
            }
        });
        if (useClouderaNaviBtn != null) {
            useClouderaNaviBtn.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    clouderaNaviButton.setEnabled(useClouderaNaviBtn.getSelection());
                    getConnection().setUseClouderaNavi(useClouderaNaviBtn.getSelection());
                    checkFieldsValue();
                }
            });
            clouderaNaviButton.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    AbstractHadoopForm form = null;
                    if (parentForm instanceof AbstractHadoopForm) {
                        form = (AbstractHadoopForm) parentForm;
                    }
                    HadoopConfsUtils.openClouderaNaviWizard(form, (HadoopClusterConnectionItem) connectionItem, creation);
                }
            });
        }

        rmSchedulerText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(final ModifyEvent e) {
                getConnection().setRmScheduler(rmSchedulerText.getText());
                checkFieldsValue();
            }
        });

        jobHistoryText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(final ModifyEvent e) {
                getConnection().setJobHistory(jobHistoryText.getText());
                checkFieldsValue();
            }
        });

        stagingDirectoryText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(final ModifyEvent e) {
                getConnection().setStagingDirectory(stagingDirectoryText.getText());
                checkFieldsValue();
            }
        });

        useDNHostBtn.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                getConnection().setUseDNHost(useDNHostBtn.getSelection());
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

        userNameText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(final ModifyEvent e) {
                getConnection().setUserName(userNameText.getText());
                checkFieldsValue();
            }
        });

        groupText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(final ModifyEvent e) {
                getConnection().setGroup(groupText.getText());
                checkFieldsValue();
            }
        });

        kerberosBtn.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                hideControl(authNodejtOrRmHistoryComposite, !kerberosBtn.getSelection());
                hideControl(authKeytabComposite, !kerberosBtn.getSelection());
                hideControl(maprTPasswordCompposite, kerberosBtn.getSelection() && maprTBtn.getSelection());
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

        maprTBtn.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                hideControl(maprTPCDCompposite, !maprTBtn.getSelection());
                hideControl(maprTSetComposite, !maprTBtn.getSelection());
                hideControl(maprTPasswordCompposite, kerberosBtn.getSelection() && maprTBtn.getSelection());
                getConnection().setEnableMaprT(maprTBtn.getSelection());
                updateForm();
                authGroup.layout();
                authGroup.getParent().layout();
                checkFieldsValue();
            }
        });
        maprTPasswordText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(final ModifyEvent e) {
                getConnection().setMaprTPassword(maprTPasswordText.getText());
                checkFieldsValue();
            }
        });
        maprTClusterText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(final ModifyEvent e) {
                getConnection().setMaprTCluster(maprTClusterText.getText());
                checkFieldsValue();
            }
        });
        maprTDurationText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(final ModifyEvent e) {
                getConnection().setMaprTDuration(maprTDurationText.getText());
                checkFieldsValue();
            }
        });
        setMaprTHomeDirBtn.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                getConnection().setSetMaprTHomeDir(setMaprTHomeDirBtn.getSelection());
                maprTHomeDirText.setText(getConnection().getMaprTHomeDir());
                updateForm();
                checkFieldsValue();
            }
        });
        maprTHomeDirText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(final ModifyEvent e) {
                getConnection().setMaprTHomeDir(maprTHomeDirText.getText());
                checkFieldsValue();
            }
        });
        setHadoopLoginBtn.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                getConnection().setSetHadoopLogin(setHadoopLoginBtn.getSelection());
                maprTHadoopLoginText.setText(getConnection().getMaprTHadoopLogin());
                updateForm();
                checkFieldsValue();
            }
        });
        maprTHadoopLoginText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(final ModifyEvent e) {
                getConnection().setMaprTHadoopLogin(maprTHadoopLoginText.getText());
                checkFieldsValue();
            }
        });
        preloadAuthentificationBtn.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                getConnection().setPreloadAuthentification(preloadAuthentificationBtn.getSelection());
                updateForm();
                checkFieldsValue();
            }
        });
        setHadoopConfBtn.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                onOverrideHadoopConfBtnSelected(e);
            }
        });
        hadoopConfSpecificJarText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                onHadoopConfPathTextModified(e);
            }
        });
        browseHadoopConfBtn.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                onBrowseHadoopConfBtnSelected(e);
            }
        });
        useWebHDFSSSLEncryptionBtn.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                hideControl(webHDFSSSLEncryptionDetailComposite, !useWebHDFSSSLEncryptionBtn.getSelection());
                getConnection().setUseWebHDFSSSL(useWebHDFSSSLEncryptionBtn.getSelection());
                updateForm();
                checkFieldsValue();
            }

        });
        webHDFSSSLTrustStorePath.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                getConnection().setWebHDFSSSLTrustStorePath(webHDFSSSLTrustStorePath.getText());
                checkFieldsValue();
            }
        });
        webHDFSSSLTrustStorePassword.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                getConnection().setWebHDFSSSLTrustStorePassword(webHDFSSSLTrustStorePassword.getText());
                checkFieldsValue();
            }
        });
        if (sparkModeCombo != null) {
            sparkModeCombo.getCombo().addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    hideFieldsOnSparkMode();
                    checkFieldsValue();
                }
            });
        }
        cloudProviderCombo.getCombo().addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                String providerLableName = cloudProviderCombo.getText();
                getConnection().getParameters().put(ConnParameterKeys.CONN_PARA_KEY_DATABRICKS_CLOUD_PROVIDER,
                        getDatabricksCloudProviderByName(providerLableName).getProviderValue());
                checkFieldsValue();
            }
        });
        runSubmitCombo.getCombo().addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                String runModeLableName = runSubmitCombo.getText();
                getConnection().getParameters().put(ConnParameterKeys.CONN_PARA_KEY_DATABRICKS_RUN_MODE,
                        getDatabricksRunModeByName(runModeLableName).getRunModeValue());
                checkFieldsValue();
            }
        });
        endpointText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(final ModifyEvent e) {
                getConnection().getParameters().put(ConnParameterKeys.CONN_PARA_KEY_DATABRICKS_ENDPOINT, endpointText.getText());
                checkFieldsValue();
            }
        });

        clusterIDText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(final ModifyEvent e) {
                getConnection().getParameters().put(ConnParameterKeys.CONN_PARA_KEY_DATABRICKS_CLUSTER_ID,
                        clusterIDText.getText());
                checkFieldsValue();
            }
        });

        tokenText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(final ModifyEvent e) {
                getConnection().getParameters().put(ConnParameterKeys.CONN_PARA_KEY_DATABRICKS_TOKEN, EncryptionUtil.getValue(tokenText.getText(), true));
                checkFieldsValue();
            }
        });

        dbfsDepFolderText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(final ModifyEvent e) {
                getConnection().getParameters().put(ConnParameterKeys.CONN_PARA_KEY_DATABRICKS_DBFS_DEP_FOLDER,
                        dbfsDepFolderText.getText());
                checkFieldsValue();
            }
        });
        
        // CDE listeners (UI to Connection)
        addBasicListener(ConnParameterKeys.CONN_PARA_KEY_CDE_API_ENDPOINT);
        addBasicListener(ConnParameterKeys.CONN_PARA_KEY_CDE_TOKEN);
        ((LabelledCheckbox) cdeAutoGenerateToken).addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                updateCdeFieldsVisibility();
                getConnection().getParameters().put(ConnParameterKeys.CONN_PARA_KEY_CDE_AUTO_GENERATE_TOKEN, Boolean.valueOf(((LabelledCheckbox) cdeAutoGenerateToken).getSelection()).toString());
            }
        });
        addBasicListener(ConnParameterKeys.CONN_PARA_KEY_CDE_TOKEN_ENDPOINT);
        addBasicListener(ConnParameterKeys.CONN_PARA_KEY_CDE_WORKLOAD_USER);
        addBasicListener(ConnParameterKeys.CONN_PARA_KEY_CDE_WORKLOAD_PASSWORD);
    }

    /*
     * Show/hide required CDE fields according token generation mechanism
     */
    private void updateCdeFieldsVisibility() {
        boolean autoGenerateToken = ((LabelledCheckbox) cdeAutoGenerateToken).getSelection();
        cdeToken.setVisible(! autoGenerateToken, autoGenerateToken);
        cdeTokenEndpoint.setVisible(autoGenerateToken, ! autoGenerateToken);
        cdeWorkloadUser.setVisible(autoGenerateToken, ! autoGenerateToken);
        cdeWorkloadPassword.setVisible(autoGenerateToken, ! autoGenerateToken);
        cdeGroup.layout();
        cdeGroup.getParent().layout();
    }

    /*
     * Add a listener to update paramKey connection parameter with value from associated widget
     */
    private void addBasicListener(String paramKey) {
        LabelledText targetComponent = (LabelledText) fieldByParamKey.get(paramKey);
        targetComponent.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(final ModifyEvent e) {
                getConnection().getParameters().put(paramKey, targetComponent.getText());
                checkFieldsValue();
            }
        });
    }

    /**
     * Hide widgets according to current Spark mode
     */
    private void hideFieldsOnSparkMode() {
        if (sparkModeCombo != null
                && "SPARK".equals(((HadoopClusterConnectionImpl) this.connectionItem.getConnection()).getDistribution())) {
            String sparkModeLabelName = sparkModeCombo.getText();
            getConnection().getParameters().put(ConnParameterKeys.CONN_PARA_KEY_SPARK_MODE,
                    getSparkModeByName(sparkModeLabelName).getValue());

            // List of possible configuration groups
            List<Group> groups = Arrays.asList(connectionGroup, authGroup, webHDFSSSLEncryptionGrp, dataBricksGroup, cdeGroup);

            // Group visibility depends on Spark mode
            Map<ESparkMode, List<Group>> visibleGroupsBySparkMode = new HashMap<ESparkMode, List<Group>>();
            visibleGroupsBySparkMode.put(ESparkMode.YARN_CLUSTER, Arrays.asList(connectionGroup, authGroup, webHDFSSSLEncryptionGrp));
            visibleGroupsBySparkMode.put(ESparkMode.DATABRICKS, Arrays.asList(dataBricksGroup));
            visibleGroupsBySparkMode.put(ESparkMode.CDE, Arrays.asList(cdeGroup));

            // Compute current visible groups
            ESparkMode currentSparkMode = ESparkMode.getByLabel(sparkModeLabelName);
            List<Group> currentVisibleGroups = visibleGroupsBySparkMode.get(currentSparkMode);
            // Hide required groups
            for (Group group : groups) {
                hideControl(group, currentVisibleGroups == null || !currentVisibleGroups.contains(group));
            }
        } else {
            hideControl(dataBricksGroup, true);
            hideControl(cdeGroup, true);
        }
    }

    private void onUseCustomConfBtnSelected(SelectionEvent event) {
        hadoopConfsButton.setEnabled(useCustomConfBtn.getSelection() && !setHadoopConfBtn.getSelection());
        getConnection().setUseCustomConfs(useCustomConfBtn.getSelection());
        refreshHadoopConfGroup();
        checkFieldsValue();
    }

    private void refreshHadoopConfGroup() {
        hideControl(hadoopConfsGroup, !useCustomConfBtn.getSelection());
        propertiesComposite.layout();
        propertiesScroll.setMinSize(propertiesComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        propertiesScroll.layout();
    }

    private void onOverrideHadoopConfBtnSelected(SelectionEvent event) {
        Boolean override = setHadoopConfBtn.getSelection();
        boolean isContextMode = isContextMode();
        hadoopConfsButton.setEnabled(!override);
        hadoopConfSpecificJarText.setEditable(override && !isContextMode);
        browseHadoopConfBtn.setEnabled(override && !isContextMode);
        if (!isContextMode) {
            getConnection().getParameters().put(ConnParameterKeys.CONN_PARA_KEY_SET_HADOOP_CONF, override.toString());
        } else {
            getConnection().getParameters().put(ConnParameterKeys.CONN_PARA_KEY_SET_HADOOP_CONF, override.toString());
        }
        checkFieldsValue();
    }

    private void onHadoopConfPathTextModified(ModifyEvent event) {
        getConnection().getParameters().put(ConnParameterKeys.CONN_PARA_KEY_HADOOP_CONF_SPECIFIC_JAR,
                hadoopConfSpecificJarText.getText());
        checkFieldsValue();
    }

    private void onBrowseHadoopConfBtnSelected(SelectionEvent event) {
        FileDialog dilaog = new FileDialog(getShell());
        dilaog.setText(getShell().getText());
        dilaog.setFilterExtensions(new String[] { "*", "*.*" }); //$NON-NLS-1$ //$NON-NLS-2$
        String filePath = dilaog.open();
        if (filePath != null && !filePath.isEmpty()) {
            String confsPath = new Path(filePath).toPortableString();
            hadoopConfSpecificJarText.setText(confsPath);
            checkFieldsValue();
        }
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
        nnProperties.setNameNode(getConnection().getNameNodeURI());
        serviceTypeToProperties.put(EHadoopServiceType.NAMENODE, nnProperties);
        HadoopServiceProperties rmORjtProperties = new HadoopServiceProperties();
        initCommonProperties(rmORjtProperties);
        if (getConnection().isUseYarn()) {
            rmORjtProperties.setResourceManager(getConnection().getJobTrackerURI());
            serviceTypeToProperties.put(EHadoopServiceType.RESOURCE_MANAGER, rmORjtProperties);
        } else {
            rmORjtProperties.setJobTracker(getConnection().getJobTrackerURI());
            serviceTypeToProperties.put(EHadoopServiceType.JOBTRACKER, rmORjtProperties);
        }
        if (getConnection().isUseCustomVersion()) {
            nnProperties.setUid(connectionItem.getProperty().getId() + ":" + ECustomVersionGroup.COMMON.getName()); //$NON-NLS-1$
            nnProperties.setCustomJars(HCVersionUtil.getCustomVersionMap(getConnection()).get(
                    ECustomVersionGroup.COMMON.getName()));
            rmORjtProperties.setUid(connectionItem.getProperty().getId() + ":" + ECustomVersionGroup.MAP_REDUCE.getName()); //$NON-NLS-1$
            rmORjtProperties.setCustomJars(HCVersionUtil.getCustomVersionMap(getConnection()).get(
                    ECustomVersionGroup.MAP_REDUCE.getName()));
        }
        new CheckHadoopServicesDialog(getShell(), filterTypes(serviceTypeToProperties)).open();
    }

    private Map<EHadoopServiceType, HadoopServiceProperties> filterTypes(
            Map<EHadoopServiceType, HadoopServiceProperties> serviceTypeToProperties) {
        Map<EHadoopServiceType, HadoopServiceProperties> filteredTypes = serviceTypeToProperties;
        IDesignerCoreService designerCoreService = CoreRuntimePlugin.getInstance().getDesignerCoreService();
        INode node = designerCoreService.getRefrenceNode("tMRConfiguration", ComponentCategory.CATEGORY_4_MAPREDUCE.getName());//$NON-NLS-1$
        if (node == null) {
            filteredTypes.remove(EHadoopServiceType.JOBTRACKER);
            filteredTypes.remove(EHadoopServiceType.RESOURCE_MANAGER);
        }
        return filteredTypes;
    }

    private void initCommonProperties(HadoopServiceProperties properties) {
        properties.setItem(this.connectionItem);
        HadoopClusterConnection connection = getConnection();
        ContextType contextType = null;
        if (getConnection().isContextMode()) {
            contextType = ConnectionContextHelper.getContextTypeForContextMode(connection, true);
        }
        properties.setContextType(contextType);
        properties.setDistribution(connection.getDistribution());
        properties.setVersion(connection.getDfVersion());
        properties.setGroup(connection.getGroup());
        properties.setUseKrb(connection.isEnableKerberos());
        properties.setCustom(connection.isUseCustomVersion());
        properties.setUseCustomConfs(connection.isUseCustomConfs());
        properties.setPrincipal(connection.getPrincipal());
        properties.setJtOrRmPrincipal(connection.getJtOrRmPrincipal());
        properties.setJobHistoryPrincipal(connection.getJobHistoryPrincipal());
        properties.setUseKeytab(connection.isUseKeytab());
        properties.setKeytabPrincipal(connection.getKeytabPrincipal());
        properties.setKeytab(connection.getKeytab());
        properties.setHadoopProperties(HadoopRepositoryUtil.getHadoopPropertiesWithOriginalValue(
                connection.getHadoopProperties(), contextType, false));
        properties.setRelativeHadoopClusterId(connectionItem.getProperty().getId());
        properties.setRelativeHadoopClusterLabel(connectionItem.getProperty().getLabel());

        properties.setMaprT(connection.isEnableMaprT());
        properties.setUserName(connection.getUserName());
        properties.setMaprTPassword(connection.getMaprTPassword());
        properties.setMaprTCluster(connection.getMaprTCluster());
        properties.setMaprTDuration(connection.getMaprTDuration());
        properties.setSetMaprTHomeDir(connection.isSetMaprTHomeDir());
        properties.setSetHadoopLogin(connection.isSetHadoopLogin());
        properties.setPreloadAuthentification(connection.isPreloadAuthentification());
        properties.setMaprTHomeDir(connection.getMaprTHomeDir());
        properties.setMaprTHadoopLogin(connection.getMaprTHadoopLogin());
        properties.setSetHadoopConf(connection.isUseCustomConfs() && HCParameterUtil.isOverrideHadoopConfs(connection));
        properties.setHadoopConfSpecificJar(ContextParameterUtils.getOriginalValue(contextType,
                Optional.ofNullable(connection.getParameters().get(ConnParameterKeys.CONN_PARA_KEY_HADOOP_CONF_SPECIFIC_JAR))
                        .orElse("")));

        properties.setUseWebHDFSSSL(connection.isUseWebHDFSSSL());
        properties.setWebHDFSSSLTrustStorePath(connection.getWebHDFSSSLTrustStorePath());
        properties.setWebHDFSSSLTrustStorePassword(connection.getWebHDFSSSLTrustStorePassword());
    }

    @Override
    public void updateForm() {
        HadoopClusterConnection connection = getConnection();
        final DistributionVersion distributionVersion = getDistributionVersion();
        if (distributionVersion.distribution.useCustom()) {
            hideControl(customGroup, false);
            String authModeName = connection.getAuthMode();
            if (authModeName != null) {
                EAuthenticationMode authMode = EAuthenticationMode.getAuthenticationByName(authModeName, false);
                switch (authMode) {
                case KRB:
                    kerberosBtn.setEnabled(true);
                    namenodePrincipalText.setEditable(kerberosBtn.isEnabled() && kerberosBtn.getSelection());
                    jtOrRmPrincipalText.setEditable(namenodePrincipalText.getEditable());
                    jobHistoryPrincipalText.setEditable(isJobHistoryPrincipalEditable());
                    keytabBtn.setEnabled(kerberosBtn.isEnabled() && kerberosBtn.getSelection());
                    keytabPrincipalText.setEditable(keytabBtn.isEnabled() && keytabBtn.getSelection());
                    keytabText.setEditable(keytabBtn.isEnabled() && keytabBtn.getSelection());
                    keytabPrincipalText.setHideWidgets(!(kerberosBtn.isEnabled() && kerberosBtn.getSelection()
                            && keytabBtn.isEnabled() && keytabBtn.getSelection()));
                    keytabText.setVisible(kerberosBtn.isEnabled() && kerberosBtn.getSelection() && keytabBtn.isEnabled()
                            && keytabBtn.getSelection());
                    userNameText.setEditable(false);
                    groupText.setEditable(false);
                    // userNameText.setHideWidgets(true);
                    userNameText.setVisible(false);
                    groupText.setHideWidgets(true);
                    hideKerberosControl(!kerberosBtn.getSelection());
                    hideMaprTicketControl(true);
                    maprTPasswordText.setEditable(false);
                    break;
                case UGI:
                    kerberosBtn.setEnabled(true);
                    namenodePrincipalText.setEditable(kerberosBtn.isEnabled() && kerberosBtn.getSelection());
                    jtOrRmPrincipalText.setEditable(namenodePrincipalText.getEditable());
                    jobHistoryPrincipalText.setEditable(namenodePrincipalText.getEditable());
                    keytabBtn.setEnabled(kerberosBtn.isEnabled() && kerberosBtn.getSelection());
                    keytabPrincipalText.setEditable(keytabBtn.isEnabled() && keytabBtn.getSelection());
                    keytabText.setEditable(keytabBtn.isEnabled() && keytabBtn.getSelection());
                    keytabPrincipalText.setHideWidgets(!(kerberosBtn.isEnabled() && kerberosBtn.getSelection()
                            && keytabBtn.isEnabled() && keytabBtn.getSelection()));
                    keytabText.setVisible(kerberosBtn.isEnabled() && kerberosBtn.getSelection() && keytabBtn.isEnabled()
                            && keytabBtn.getSelection());
                    userNameText.setEditable(!(kerberosBtn.isEnabled() && kerberosBtn.getSelection()));
                    groupText.setEditable(true);
                    userNameText.setHideWidgets(kerberosBtn.isEnabled() && kerberosBtn.getSelection());
                    groupText.setHideWidgets(false);
                    hideKerberosControl(!kerberosBtn.getSelection());
                    // maprt
                    hideMaprTicketChildControl(!maprTBtn.getSelection());
                    maprTPasswordText.setEditable(maprTBtn.isEnabled()
                            && (maprTBtn.getSelection() && !(kerberosBtn.isEnabled() && kerberosBtn.getSelection())));
                    break;
                default:
                    kerberosBtn.setEnabled(false);
                    namenodePrincipalText.setEditable(false);
                    jtOrRmPrincipalText.setEditable(false);
                    jobHistoryPrincipalText.setEditable(false);
                    keytabBtn.setEnabled(false);
                    keytabPrincipalText.setEditable(false);
                    keytabText.setEditable(false);
                    userNameText.setEditable(true);
                    groupText.setEditable(false);
                    userNameText.setHideWidgets(false);
                    groupText.setHideWidgets(true);
                    hideKerberosControl(true);
                    hideMaprTicketControl(true);
                    maprTPasswordText.setEditable(false);
                    break;
                }
            }
        } else {
            hideControl(customGroup, true);

            kerberosBtn.setEnabled(isCurrentHadoopVersionSupportSecurity());
            namenodePrincipalText.setEditable(kerberosBtn.isEnabled() && kerberosBtn.getSelection());
            jtOrRmPrincipalText.setEditable(namenodePrincipalText.getEditable());
            jobHistoryPrincipalText.setEditable(isJobHistoryPrincipalEditable());
            keytabBtn.setEnabled(kerberosBtn.isEnabled() && kerberosBtn.getSelection());
            keytabPrincipalText.setEditable(keytabBtn.isEnabled() && keytabBtn.getSelection());
            keytabText.setEditable(keytabBtn.isEnabled() && keytabBtn.getSelection());
            keytabPrincipalText
                    .setHideWidgets(!(kerberosBtn.isEnabled() && kerberosBtn.getSelection() && keytabBtn.isEnabled() && keytabBtn
                            .getSelection()));
            keytabText.setVisible(kerberosBtn.isEnabled() && kerberosBtn.getSelection() && keytabBtn.isEnabled()
                    && keytabBtn.getSelection());
            groupText.setEditable(isCurrentHadoopVersionSupportGroup());
            userNameText.setEditable(!kerberosBtn.getSelection());
            if (isCurrentHadoopVersionSupportGroup()) {
                userNameText.setHideWidgets(kerberosBtn.getSelection());
            } else {
                userNameText.setVisible(!kerberosBtn.getSelection());
            }
            groupText.setHideWidgets(!isCurrentHadoopVersionSupportGroup());
            hideKerberosControl(!kerberosBtn.getSelection());

            // maprt
            hideControl(maprTBtn, !isCurrentHadoopVersionSupportMapRTicket());
            maprTBtn.setEnabled(isCurrentHadoopVersionSupportMapRTicket());
            maprTPasswordText.setEditable(maprTBtn.isEnabled()
                    && (maprTBtn.getSelection() && !(kerberosBtn.isEnabled() && kerberosBtn.getSelection())));
            maprTClusterText.setEditable(maprTBtn.isEnabled() && maprTBtn.getSelection());
            maprTDurationText.setEditable(maprTBtn.isEnabled() && maprTBtn.getSelection());
            setMaprTHomeDirBtn.setEnabled(maprTBtn.isEnabled() && maprTBtn.getSelection());
            setHadoopLoginBtn.setEnabled(maprTBtn.isEnabled() && maprTBtn.getSelection());
            preloadAuthentificationBtn.setEnabled(maprTBtn.isEnabled() && maprTBtn.getSelection());
            hideMaprTicketChildControl(!maprTBtn.getSelection());
        }
        updateMRRelatedContent();
        updateConnectionContent();
        hideWebHDFSControl(!isCurrentHadoopVersionSupportWebHDFS());
        if (isContextMode()) {
            adaptFormToEditable();
        }
        hideFieldsOnSparkMode();
    }

    private void hideMaprTicketControl(boolean hide) {
        maprTClusterText.setEditable(!hide);
        maprTDurationText.setEditable(!hide);
        hideControl(maprTBtn, hide);
        hideControl(maprTPCDCompposite, hide);
        hideControl(maprTSetComposite, hide);
        authMaprTComposite.layout();
        authMaprTComposite.getParent().layout();
    }

    private void hideMaprTicketChildControl(boolean hide) {
        maprTClusterText.setEditable(maprTBtn.isEnabled() && maprTBtn.getSelection());
        maprTDurationText.setEditable(maprTBtn.isEnabled() && maprTBtn.getSelection());
        maprTHomeDirText.setEditable(!hide && setMaprTHomeDirBtn.isEnabled() && setMaprTHomeDirBtn.getSelection());
        maprTHadoopLoginText.setEditable(!hide && setHadoopLoginBtn.isEnabled() && setHadoopLoginBtn.getSelection());
        hideControl(maprTPasswordCompposite, kerberosBtn.getSelection() && maprTBtn.getSelection());
        hideControl(maprTPCDCompposite, hide);
        hideControl(maprTSetComposite, hide);
        authMaprTComposite.layout();
        authMaprTComposite.getParent().layout();
    }

    private void hideKerberosControl(boolean hide) {
        hideControl(authNodejtOrRmHistoryComposite, hide);
        hideControl(authKeytabComposite, hide);
        authCommonComposite.layout();
        authCommonComposite.getParent().layout();
    }

    private void hideWebHDFSControl(boolean hide) {
        hideControl(webHDFSSSLEncryptionGrp, hide);
        useWebHDFSSSLEncryptionBtn.setEnabled(!hide);
        webHDFSSSLTrustStorePath.setEditable(useWebHDFSSSLEncryptionBtn.isEnabled() && useWebHDFSSSLEncryptionBtn.getSelection());
        webHDFSSSLTrustStorePassword
                .setEditable(useWebHDFSSSLEncryptionBtn.isEnabled() && useWebHDFSSSLEncryptionBtn.getSelection());
        webHDFSSSLTrustStorePath.setVisible(useWebHDFSSSLEncryptionBtn.isEnabled() && useWebHDFSSSLEncryptionBtn.getSelection());
        webHDFSSSLTrustStorePassword
                .setHideWidgets(!(useWebHDFSSSLEncryptionBtn.isEnabled() && useWebHDFSSSLEncryptionBtn.getSelection()));
    }

    private boolean isJobHistoryPrincipalEditable() {
        return isCurrentHadoopVersionSupportJobHistoryPrincipal()
                && getConnection().isEnableKerberos()
                && (!hadoopDistribution.useCustom() || hadoopDistribution.useCustom() && isCustomUnsupportHasSecurity()
                        && getConnection().isUseYarn());
    }

    private boolean isCurrentHadoopVersionSupportJobHistoryPrincipal() {
        return isCurrentHadoopVersionSupportSecurity() && (isCurrentHadoopVersionSupportYarn() || hadoopDistribution.useCustom());
    }

    private DistributionBean getDistribution() {
        return HadoopDistributionsHelper.HADOOP.getDistribution(getConnection().getDistribution(), false);
    }

    private DistributionVersion getDistributionVersion() {
        final DistributionBean distribution = getDistribution();
        if (distribution != null) {
            return distribution.getVersion(getConnection().getDfVersion(), false);
        }
        return null;
    }

    private boolean isCurrentHadoopVersionSupportGroup() {
        boolean supportGroup = false;
        final DistributionVersion distributionVersion = getDistributionVersion();
        if (distributionVersion != null && distributionVersion.hadoopComponent != null) {
            supportGroup = distributionVersion.hadoopComponent.doSupportGroup();
        }
        return supportGroup;
    }

    private boolean isCustomUnsupportHasGroup() {
        EAuthenticationMode authMode = EAuthenticationMode.getAuthenticationByName(getConnection().getAuthMode(), false);
        return authMode.equals(EAuthenticationMode.UGI);
    }

    private boolean isCustomUnsupportHasSecurity() {
        EAuthenticationMode authMode = EAuthenticationMode.getAuthenticationByName(getConnection().getAuthMode(), false);
        return authMode.equals(EAuthenticationMode.KRB);
    }

    private boolean isCurrentHadoopVersionSupportSecurity() {
        boolean supportSecurity = false;
        final DistributionVersion distributionVersion = getDistributionVersion();
        if (distributionVersion != null && distributionVersion.hadoopComponent != null) {
            supportSecurity = distributionVersion.hadoopComponent.doSupportKerberos();
        }
        return supportSecurity;
    }

    private boolean isCurrentHadoopVersionSupportMapRTicket() {
        boolean supportMapRTicket = false;
        final DistributionVersion distributionVersion = getDistributionVersion();
        if (distributionVersion != null && distributionVersion.hadoopComponent != null) {
            supportMapRTicket = distributionVersion.hadoopComponent.doSupportMapRTicket();
        }
        return supportMapRTicket;
    }

    private boolean isCurrentHadoopVersionSupportYarn() {
        boolean supportYarn = false;
        final DistributionVersion distributionVersion = getDistributionVersion();
        if (distributionVersion != null && distributionVersion.hadoopComponent != null) {
            supportYarn = distributionVersion.hadoopComponent.isHadoop2() || distributionVersion.hadoopComponent.isHadoop3();
        }
        return supportYarn;
    }

    private boolean isCurrentHadoopVersionSupportWebHDFS() {
        boolean supportWebHDFS = false;
        final DistributionVersion distributionVersion = getDistributionVersion();
        if (distributionVersion != null && distributionVersion.hadoopComponent != null) {
            supportWebHDFS = distributionVersion.hadoopComponent.doSupportWebHDFS();
        }
        return supportWebHDFS;
    }

    private void updateMRRelatedContent() {
        boolean useYarn = getConnection().isUseYarn();
        jobtrackerUriText
        .setLabelText(useYarn ? Messages.getString("HadoopClusterForm.text.resourceManager") : Messages.getString("HadoopClusterForm.text.jobtrackerURI")); //$NON-NLS-1$ //$NON-NLS-2$
jobtrackerUriText.getTextControl().getParent().layout();
jtOrRmPrincipalText
        .setLabelText(useYarn ? Messages.getString("HadoopClusterForm.text.rmPrincipal") : Messages.getString("HadoopClusterForm.text.jtPrincipal")); //$NON-NLS-1$ //$NON-NLS-2$
    jtOrRmPrincipalText.getTextControl().getParent().layout();
    }

    private void updateConnectionContent() {
        if (!kerberosBtn.isEnabled()) {
            kerberosBtn.setSelection(false);
            namenodePrincipalText.setText(EMPTY_STRING);
            jtOrRmPrincipalText.setText(EMPTY_STRING);
            jobHistoryPrincipalText.setText(EMPTY_STRING);
            getConnection().setEnableKerberos(false);
        }
        if (!maprTBtn.isEnabled()) {
            maprTBtn.setSelection(false);
            maprTPasswordText.setText(EMPTY_STRING);
            maprTClusterText.setText(EMPTY_STRING);
            maprTDurationText.setText(EMPTY_STRING);
            setMaprTHomeDirBtn.setSelection(false);
            setHadoopLoginBtn.setSelection(false);
            preloadAuthentificationBtn.setSelection(false);
            maprTHomeDirText.setText(EMPTY_STRING);
            maprTHadoopLoginText.setText(EMPTY_STRING);
            getConnection().setEnableMaprT(false);
        }
        if (!groupText.getEditable()) {
            groupText.setText(EMPTY_STRING);
        }
        if (!userNameText.getEditable()) {
            userNameText.setText(EMPTY_STRING);
        }
        if (!useWebHDFSSSLEncryptionBtn.isEnabled()) {
            useWebHDFSSSLEncryptionBtn.setSelection(false);
            webHDFSSSLTrustStorePath.setText(EMPTY_STRING);
            webHDFSSSLTrustStorePassword.setText(EMPTY_STRING);
            getConnection().setUseWebHDFSSSL(false);
        }
    }

    @Override
    protected void hideControl(Control control, boolean hide) {
        Object layoutData = control.getLayoutData();
        if (layoutData instanceof GridData) {
            GridData data = (GridData) layoutData;
            data.exclude = hide;
            control.setLayoutData(data);
            control.setVisible(!hide);
            if (control.getParent() != null) {
                control.getParent().layout();
            }
        }
    }

    private void fillDefaults() {
        HadoopClusterConnection connection = getConnection();
        if (creation && !connection.isUseCustomConfs()) {
            HCRepositoryUtil.fillDefaultValuesOfHadoopCluster(connection);
        }
    }

    @Override
    public boolean checkFieldsValue() {
        checkServicesBtn.setEnabled(false);
        updateStatus(IStatus.OK, null);

        if (!"SPARK".equals(((HadoopClusterConnectionImpl) this.connectionItem.getConnection()).getDistribution())
                || (sparkModeCombo != null && ESparkMode.YARN_CLUSTER.getLabel().equals(sparkModeCombo.getText()))) {
            if (getConnection().isUseCustomVersion()) {
                if (authenticationCombo.getSelectionIndex() == -1) {
                    updateStatus(IStatus.ERROR, Messages.getString("HadoopClusterForm.check.authentication")); //$NON-NLS-1$
                    return false;
                }
            }

            if (!validText(namenodeUriText.getText())) {
                updateStatus(IStatus.ERROR, Messages.getString("HadoopClusterForm.check.namenodeURI")); //$NON-NLS-1$
                return false;
            }

            if (!isContextMode() && !HadoopParameterValidator.isValidNamenodeURI(namenodeUriText.getText())) {
                updateStatus(IStatus.ERROR, Messages.getString("HadoopClusterForm.check.namenodeURI.invalid")); //$NON-NLS-1$
                return false;
            }

            if (!validText(jobtrackerUriText.getText())) {
                updateStatus(IStatus.ERROR,
                        Messages.getString("HadoopClusterForm.check.jobtrackerURI2", jobtrackerUriText.getLabelText())); //$NON-NLS-1$
                return false;
            }

            if (!isContextMode() && !HadoopParameterValidator.isValidJobtrackerURI(jobtrackerUriText.getText())) {
                updateStatus(IStatus.ERROR,
                        Messages.getString("HadoopClusterForm.check.jobtrackerURI.invalid2", jobtrackerUriText.getLabelText())); //$NON-NLS-1$
                return false;
            }

            if (namenodePrincipalText.getEditable()) {
                if (!validText(namenodePrincipalText.getText())) {
                    updateStatus(IStatus.ERROR, Messages.getString("HadoopClusterForm.check.namenodePrincipal")); //$NON-NLS-1$
                    return false;
                }
                if (!isContextMode() && !HadoopParameterValidator.isValidPrincipal(namenodePrincipalText.getText())) {
                    updateStatus(IStatus.ERROR, Messages.getString("HadoopClusterForm.check.namenodePrincipal.invalid")); //$NON-NLS-1$
                    return false;
                }
            }

            if (jtOrRmPrincipalText.getEditable()) {
                if (!validText(jtOrRmPrincipalText.getText())) {
                    updateStatus(IStatus.ERROR,
                            Messages.getString("HadoopClusterForm.check.jtOrRmPrincipal", jtOrRmPrincipalText.getLabelText())); //$NON-NLS-1$
                    return false;
                }
                if (!isContextMode() && !HadoopParameterValidator.isValidPrincipal(jtOrRmPrincipalText.getText())) {
	                updateStatus(IStatus.ERROR,
	                        Messages.getString("HadoopClusterForm.check.jtOrRmPrincipal.invalid", jtOrRmPrincipalText.getLabelText())); //$NON-NLS-1$
                    return false;
                }
            }

            if (jobHistoryPrincipalText.getEditable()) {
                if (!validText(jobHistoryPrincipalText.getText())) {
                    updateStatus(IStatus.ERROR, Messages.getString("HadoopClusterForm.check.jobHistoryPrincipal")); //$NON-NLS-1$
                    return false;
                }
                if (!isContextMode() && !HadoopParameterValidator.isValidPrincipal(jobHistoryPrincipalText.getText())) {
                    updateStatus(IStatus.ERROR, Messages.getString("HadoopClusterForm.check.jobHistoryPrincipal.invalid")); //$NON-NLS-1$
                    return false;
                }
            }

            if (userNameText != null && userNameText.getEditable()) {
                if (!validText(userNameText.getText())) {
                    updateStatus(IStatus.ERROR, Messages.getString("HadoopClusterForm.check.userName")); //$NON-NLS-1$
                    return false;
                }
            }

            if (groupText.getEditable()) {
                if (!validText(groupText.getText())) {
                    updateStatus(IStatus.ERROR, Messages.getString("HadoopClusterForm.check.group")); //$NON-NLS-1$
                    return false;
                }
                if (!isContextMode() && !HadoopParameterValidator.isValidGroup(groupText.getText())) {
                    updateStatus(IStatus.ERROR, Messages.getString("HadoopClusterForm.check.group.invalid")); //$NON-NLS-1$
                    return false;
                }
            }

            if (validText(userNameText.getText()) && !HadoopParameterValidator.isValidUserName(userNameText.getText())) {
                updateStatus(IStatus.ERROR, Messages.getString("HadoopClusterForm.check.userName.invalid")); //$NON-NLS-1$
                return false;
            }

            if (keytabPrincipalText.getEditable()) {
                if (!validText(keytabPrincipalText.getText())) {
                    updateStatus(IStatus.ERROR, Messages.getString("HadoopClusterForm.check.keytabPrincipal")); //$NON-NLS-1$
                    return false;
                }
                if (!isContextMode() && !HadoopParameterValidator.isValidPrincipal(keytabPrincipalText.getText())) {
                    updateStatus(IStatus.ERROR, Messages.getString("HadoopClusterForm.check.keytabPrincipal.invalid")); //$NON-NLS-1$
                    return false;
                }
            }

            if (keytabText.getEditable()) {
                if (!validText(keytabText.getText())) {
                    updateStatus(IStatus.ERROR, Messages.getString("HadoopClusterForm.check.keytab")); //$NON-NLS-1$
                    return false;
                }
            }

            if (maprTPasswordText.getEditable()) {
                if (!validText(maprTPasswordText.getText())) {
                    updateStatus(IStatus.ERROR, Messages.getString("HadoopClusterForm.check.maprTPassword")); //$NON-NLS-1$
                    return false;
                }
                if (!isContextMode() && !HadoopParameterValidator.isValidMaprTPassword(maprTPasswordText.getText())) {
                    updateStatus(IStatus.ERROR, Messages.getString("HadoopClusterForm.check.maprTPassword.invalid")); //$NON-NLS-1$
                    return false;
                }
            }

            if (maprTClusterText.getEditable()) {
                if (!validText(maprTClusterText.getText())) {
                    updateStatus(IStatus.ERROR, Messages.getString("HadoopClusterForm.check.maprTCluster")); //$NON-NLS-1$
                    return false;
                }
                if (!isContextMode() && !HadoopParameterValidator.isValidMaprTCluster(maprTClusterText.getText())) {
                    updateStatus(IStatus.ERROR, Messages.getString("HadoopClusterForm.check.maprTCluster.invalid")); //$NON-NLS-1$
                    return false;
                }
            }

            if (maprTDurationText.getEditable()) {
                if (!validText(maprTDurationText.getText())) {
                    updateStatus(IStatus.ERROR, Messages.getString("HadoopClusterForm.check.maprTDuration")); //$NON-NLS-1$
                    return false;
                }
                if (!isContextMode() && !HadoopParameterValidator.isValidMaprTDuration(maprTDurationText.getText())) {
                    updateStatus(IStatus.ERROR, Messages.getString("HadoopClusterForm.check.maprTDuration.invalid")); //$NON-NLS-1$
                    return false;
                }
            }

            if (webHDFSSSLTrustStorePassword.getEditable()) {
                if (!validText(webHDFSSSLTrustStorePassword.getText())) {
                    updateStatus(IStatus.ERROR, Messages.getString("HadoopClusterForm.webHDFS.check.trustStorePassword")); //$NON-NLS-1$
                    return false;
                }
	            if (!isContextMode()
	                    && !HadoopParameterValidator.isValidWebHDFSSSLTrustStorePassword(webHDFSSSLTrustStorePassword.getText())) {
                    updateStatus(IStatus.ERROR, Messages.getString("HadoopClusterForm.webHDFS.check.trustStorePassword.invalid")); //$NON-NLS-1$
                    return false;
                }
            }

            if (webHDFSSSLTrustStorePath.getEditable()) {
                if (!validText(webHDFSSSLTrustStorePath.getText())) {
                    updateStatus(IStatus.ERROR, Messages.getString("HadoopClusterForm.webHDFS.check.trustStorePath")); //$NON-NLS-1$
                    return false;
                }
            }
            checkServicesBtn.setEnabled(true);
        }
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
        if (isContextMode()) {
            adaptFormToEditable();
        }
        if (isReadOnly() != readOnly) {
            adaptFormToReadOnly();
        }
        if (visible) {
            updateStatus(getStatusLevel(), getStatus());
        }
    }

    @Override
    protected void collectConParameters() {
        if (!"SPARK".equals(((HadoopClusterConnectionImpl) this.connectionItem.getConnection()).getDistribution())) {
            collectYarnConParameters();
        } else {
            String sparkModeLabelName = sparkModeCombo.getText();
            if (ESparkMode.YARN_CLUSTER.getLabel().equals(sparkModeLabelName)) {
                collectYarnConParameters();
            } else if (ESparkMode.DATABRICKS.getLabel().equals(sparkModeLabelName)) {
                collectDBRParameters();
            } else if (ESparkMode.KUBERNETES.getLabel().equals(sparkModeLabelName)) {
                // TODO
            } else if (ESparkMode.CDE.getLabel().equals(sparkModeLabelName)) {
                collectCDEParameters();
            }
        }
    }

    private void collectCDEParameters() {
        addContextParams(EHadoopParamName.CdeApiEndPoint, true);
        addContextParams(EHadoopParamName.CdeToken, true);
        addContextParams(EHadoopParamName.CdeTokenEndpoint, true);
        addContextParams(EHadoopParamName.CdeWorkloadUser, true);
        addContextParams(EHadoopParamName.CdeWorkloadPassword, true);
    }

    protected void collectDBRParameters() {
        collectConfigurationParameters(true);
    }

    private void collectConfigurationParameters(boolean isUse) {
        addContextParams(EHadoopParamName.DataBricksEndpoint, isUse);
        addContextParams(EHadoopParamName.DataBricksClusterId, isUse);
        addContextParams(EHadoopParamName.DataBricksToken, isUse);
        addContextParams(EHadoopParamName.DataBricksDBFSDepFolder, isUse);
    }

    protected void collectYarnConParameters() {
        collectConFieldContextParameters(
                isCurrentHadoopVersionSupportYarn() || getConnection().isUseYarn());
        collectWebHDFSSSLContextParameters(useWebHDFSSSLEncryptionBtn.getSelection());
        collectAuthFieldContextParameters(kerberosBtn.getSelection());
        collectKeyTabContextParameters(kerberosBtn.getSelection() && keytabBtn.getSelection());
        collectAuthMaprTFieldContextParameters(maprTBtn.getSelection());
        if (useClouderaNaviBtn != null) {
            collectClouderaNavigatorFieldContextParameters(useClouderaNaviBtn.getSelection());
        }
        collectOverrideHadoopConfsContextParameters();
    }

    private void collectOverrideHadoopConfsContextParameters() {
        // addContextParams(EHadoopParamName.setHadoopConf, true); // not support yet
        addContextParams(EHadoopParamName.hadoopConfSpecificJar, true);
    }

    private void collectClouderaNavigatorFieldContextParameters(boolean clouderaNav) {
        addContextParams(EHadoopParamName.ClouderaNavigatorUsername, clouderaNav);
        addContextParams(EHadoopParamName.ClouderaNavigatorPassword, clouderaNav);
        addContextParams(EHadoopParamName.ClouderaNavigatorUrl, clouderaNav);
        addContextParams(EHadoopParamName.ClouderaNavigatorMetadataUrl, clouderaNav);
        addContextParams(EHadoopParamName.ClouderaNavigatorClientUrl, clouderaNav);
    }

    private void collectConFieldContextParameters(boolean useYarn) {
        addContextParams(EHadoopParamName.NameNodeUri, true);
        addContextParams(EHadoopParamName.JobTrackerUri, !useYarn);
        addContextParams(EHadoopParamName.ResourceManager, useYarn);
        addContextParams(EHadoopParamName.ResourceManagerScheduler, true);
        addContextParams(EHadoopParamName.JobHistory, true);
        addContextParams(EHadoopParamName.StagingDirectory, true);
    }

    private void collectWebHDFSSSLContextParameters(boolean useWebHDFSSSL) {
        addContextParams(EHadoopParamName.WebHDFSSSLTrustStorePath, useWebHDFSSSL);
        addContextParams(EHadoopParamName.WebHDFSSSLTrustStorePassword, useWebHDFSSSL);
    }

    private void collectAuthFieldContextParameters(boolean useKerberos) {
        addContextParams(EHadoopParamName.NameNodePrin, useKerberos);
        addContextParams(EHadoopParamName.JTOrRMPrin, useKerberos);
        addContextParams(EHadoopParamName.JobHistroyPrin, useKerberos);
        addContextParams(EHadoopParamName.User, !useKerberos);
        addContextParams(EHadoopParamName.Group, !useKerberos
                && (isCurrentHadoopVersionSupportGroup() || isCustomUnsupportHasGroup()));
    }

    private void collectKeyTabContextParameters(boolean useKeyTab) {
        addContextParams(EHadoopParamName.Principal, useKeyTab);
        addContextParams(EHadoopParamName.KeyTab, useKeyTab);
    }

    private void collectAuthMaprTFieldContextParameters(boolean useMaprT) {
        addContextParams(EHadoopParamName.maprTPassword, useMaprT);
        addContextParams(EHadoopParamName.maprTCluster, useMaprT);
        addContextParams(EHadoopParamName.maprTDuration, useMaprT);
        addContextParams(EHadoopParamName.maprTHomeDir, useMaprT);
        addContextParams(EHadoopParamName.maprTHadoopLogin, useMaprT);
    }

    @Override
    protected void exportAsContext() {
        super.exportAsContext();
        HadoopConfsUtils.updateContextualHadoopConfs((HadoopClusterConnectionItem) this.connectionItem);
    }

    private List<String> getSparkModes() {
        List<String> sparkModeNames = new ArrayList<String>();
        if (sparkDistribution != null) {
            List<ESparkMode> sparkModes = sparkDistribution.getSparkModes();
            if (sparkModes != null) {
                sparkModeNames = sparkModes.stream().map(mode -> {
                    return mode.getLabel();
                }).collect(Collectors.toList());
            }
        }
        return sparkModeNames;
    }

    private ESparkMode getSparkModeByName(String sparkModeName) {
        if (sparkDistribution != null) {
            List<ESparkMode> supportRunModes = sparkDistribution.getSparkModes();
            for (ESparkMode provider : supportRunModes) {
                if (StringUtils.equals(provider.getLabel(), sparkModeName)) {
                    return provider;
                }
            }
        }
        return ESparkMode.YARN_CLUSTER;
    }

    private ESparkMode getSparkModeByValue(String sparkModeValue) {
        if (sparkDistribution != null) {
            List<ESparkMode> runModes = sparkDistribution.getSparkModes();
            for (ESparkMode runMode : runModes) {
                if (StringUtils.equals(runMode.getValue(), sparkModeValue)) {
                    return runMode;
                }
            }
        }
        return ESparkMode.YARN_CLUSTER;
    }

    private EDatabriksCloudProvider getDatabricksCloudProviderByValue(String providerValue) {
        if (sparkDistribution != null) {
            List<EDatabriksCloudProvider> supportCloudProviders = sparkDistribution.getSupportCloudProviders();
            for (EDatabriksCloudProvider provider : supportCloudProviders) {
                if (StringUtils.equals(provider.getProviderValue(), providerValue)) {
                    return provider;
                }
            }
        }
        return EDatabriksCloudProvider.AWS;
    }

    private EDatabriksSubmitMode getDatabricksRunModeByValue(String runModeValue) {
        if (sparkDistribution != null) {
            List<EDatabriksSubmitMode> runModes = sparkDistribution.getRunSubmitMode();
            for (EDatabriksSubmitMode runMode : runModes) {
                if (StringUtils.equals(runMode.getRunModeValue(), runModeValue)) {
                    return runMode;
                }
            }
        }
        return EDatabriksSubmitMode.CREATE_RUN_JOB;
    }

    private EDatabriksCloudProvider getDatabricksCloudProviderByName(String providerLableName) {
        if (sparkDistribution != null) {
            List<EDatabriksCloudProvider> supportCloudProviders = sparkDistribution.getSupportCloudProviders();
            for (EDatabriksCloudProvider provider : supportCloudProviders) {
                if (StringUtils.equals(provider.getProviderLableName(), providerLableName)) {
                    return provider;
                }
            }
        }
        return EDatabriksCloudProvider.AWS;
    }

    private EDatabriksSubmitMode getDatabricksRunModeByName(String runModeLableName) {
        if (sparkDistribution != null) {
            List<EDatabriksSubmitMode> supportRunModes = sparkDistribution.getRunSubmitMode();
            for (EDatabriksSubmitMode provider : supportRunModes) {
                if (StringUtils.equals(provider.getRunModeLabel(), runModeLableName)) {
                    return provider;
                }
            }
        }
        return EDatabriksSubmitMode.CREATE_RUN_JOB;
    }
}
