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

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.window.Window;
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
import org.talend.commons.ui.runtime.image.EImage;
import org.talend.commons.ui.runtime.image.ImageProvider;
import org.talend.commons.ui.swt.formtools.Form;
import org.talend.commons.ui.swt.formtools.LabelledCombo;
import org.talend.core.database.conn.ConnParameterKeys;
import org.talend.core.hadoop.version.custom.HadoopCustomVersionDefineDialog;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.hadoop.distribution.helper.HadoopDistributionsHelper;
import org.talend.hadoop.distribution.model.DistributionBean;
import org.talend.hadoop.distribution.model.DistributionVersion;
import org.talend.repository.hadoopcluster.i18n.Messages;
import org.talend.repository.hadoopcluster.ui.common.AbstractHadoopForm;
import org.talend.repository.hadoopcluster.ui.common.IHadoopClusterInfoForm;
import org.talend.repository.hadoopcluster.util.HCVersionUtil;
import org.talend.repository.model.hadoopcluster.HadoopClusterConnection;

/**
 * DOC ycbai class global comment. Detailled comment
 */
public class HadoopClusterForm extends AbstractHadoopForm<HadoopClusterConnection> {

    private static final int VISIBLE_DISTRIBUTION_COUNT = 5;

    private static final int VISIBLE_VERSION_COUNT = 6;

    private String[] existingNamesArray;

    private LabelledCombo distributionCombo;

    private LabelledCombo versionCombo;

    private Button customButton;

    private Button useYarnButton;

    private Button useKnoxButton;

    private IHadoopClusterInfoForm hcInfoForm;

    private final boolean creation;

    public HadoopClusterForm(Composite parent, ConnectionItem connectionItem, String[] existingNames, boolean creation) {
        super(parent, SWT.NONE, existingNames);
        this.connectionItem = connectionItem;
        this.creation = creation;
        existingNamesArray = existingNames;
        setConnectionItem(connectionItem);
        setupForm();
        init();
        setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout layout = (GridLayout) getLayout();
        layout.marginHeight = 0;
        setLayout(layout);
    }

    public void init() {
        final DistributionBean distribution = HadoopDistributionsHelper.HADOOP.getDistribution(getConnection()
                .getDistribution(), false);
        if (distribution != null) {
            distributionCombo.setText(distribution.displayName);
        } else {
            distributionCombo.select(0);
        }
        updateVersionPart();
        updateKnoxPart();

        useYarnButton.setSelection(getConnection().isUseYarn());

        String useKnoxStr = getConnection().getParameters().get(ConnParameterKeys.CONN_PARA_KEY_USE_KNOX);
        useKnoxButton.setSelection("true".equals(useKnoxStr));
        switchToInfoForm();

        updateStatus(IStatus.OK, EMPTY_STRING);
    }

    @Override
    public void reload() {
        init();
        if (hcInfoForm != null) {
            hcInfoForm.init();
        }
    }

    @Override
    protected void adaptFormToReadOnly() {
        readOnly = isReadOnly();
        distributionCombo.setReadOnly(readOnly);
        versionCombo.setReadOnly(readOnly);
    }

    @Override
    protected void updateEditableStatus(boolean isEditable) {
        distributionCombo.setEnabled(isEditable);
        versionCombo.setEnabled(isEditable);
        useYarnButton.setEnabled(isEditable);
        useKnoxButton.setEnabled(isEditable);
    }

    @Override
    protected void addFields() {
        addVersionFields();
    }

    private void addVersionFields() {
        Group distributionGroup = Form.createGroup(this, 4, Messages.getString("HadoopClusterForm.versionSettings")); //$NON-NLS-1$
        distributionGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        distributionCombo = new LabelledCombo(distributionGroup,
                Messages.getString("HadoopClusterForm.distribution"), //$NON-NLS-1$
                Messages.getString("HadoopClusterForm.distribution.tooltip"), HadoopDistributionsHelper.HADOOP.getDistributionsDisplay(true), //$NON-NLS-1$
                1, true);
        distributionCombo.setVisibleItemCount(VISIBLE_DISTRIBUTION_COUNT);
        versionCombo = new LabelledCombo(distributionGroup, Messages.getString("HadoopClusterForm.version"), //$NON-NLS-1$
                Messages.getString("HadoopClusterForm.version.tooltip"), new String[0], 1, true); //$NON-NLS-1$
        versionCombo.setVisibleItemCount(VISIBLE_VERSION_COUNT);
        customButton = new Button(distributionGroup, SWT.NULL);
        customButton.setImage(ImageProvider.getImage(EImage.THREE_DOTS_ICON));
        customButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false, 2, 1));
        useYarnButton = new Button(distributionGroup, SWT.CHECK);
        useYarnButton.setText(Messages.getString("HadoopClusterForm.useYarn")); //$NON-NLS-1$
        useYarnButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false, 2, 1));
        useKnoxButton = new Button(distributionGroup, SWT.CHECK);
        useKnoxButton.setText(Messages.getString("HadoopClusterForm.useKnox")); //$NON-NLS-1$
        useKnoxButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false, 2, 1));
    }

    @Override
    protected void addFieldsListeners() {
        distributionCombo.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(final ModifyEvent e) {
                String newDistributionDisplayName = distributionCombo.getText();
                DistributionBean newDistribution = HadoopDistributionsHelper.HADOOP.getDistribution(newDistributionDisplayName,
                        true);
                String newDistributionName = newDistribution.name;
                String originalDistributionName = getConnection().getDistribution();
                getConnection().setDistribution(newDistributionName);
                getConnection().setUseCustomVersion(newDistribution.useCustom());
                boolean distrChanged = !StringUtils.equals(newDistributionName, originalDistributionName);
                if (distrChanged) {
                    getConnection().setDfVersion(null);
                }
                updateVersionPart();
                updateKnoxPart();
                updateYarnContent();
                updateKnoxContent();
                switchToInfoForm();
                checkFieldsValue();

            }
        });

        versionCombo.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(final ModifyEvent e) {
                String newVersionDisplayName = versionCombo.getText();
                if (StringUtils.isEmpty(newVersionDisplayName)) {
                    return;
                }
                DistributionBean hadoopDistribution = HadoopDistributionsHelper.HADOOP.getDistribution(
                        distributionCombo.getText(), true);
                DistributionVersion hadoopVersion = hadoopDistribution.getVersion(newVersionDisplayName, true);

                getConnection().setDfVersion(hadoopVersion.version);
                if (hadoopVersion.hadoopComponent.isHadoop2() || hadoopVersion.hadoopComponent.isHadoop3()) {
                    getConnection().setUseYarn(true);
                } else {
                    getConnection().setUseYarn(false);
                }
                updateKnoxPart();
                updateYarnContent();
                updateKnoxContent();
                switchToInfoForm();
                checkFieldsValue();
            }
        });

        customButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                HadoopCustomVersionDefineDialog customVersionDialog = new HadoopCustomVersionDefineDialog(getShell(),
                        HCVersionUtil.getCustomVersionMap(getConnection()));
                if (customVersionDialog.open() == Window.OK) {
                    HCVersionUtil.injectCustomVersionMap(getConnection(), customVersionDialog.getLibMap());
                }
            }
        });

        useYarnButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                getConnection().setUseYarn(useYarnButton.getSelection());
                switchToInfoForm();
            }
        });

        useKnoxButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {

                String selection = String.valueOf(useKnoxButton.getSelection());

                getConnection().getParameters().put(ConnParameterKeys.CONN_PARA_KEY_USE_KNOX, selection);
                switchToInfoForm();
                checkFieldsValue();
            }
        });

    }

    @Override
    public void setListener(ICheckListener listener) {
        super.setListener(listener);
        if (hcInfoForm != null) {
            hcInfoForm.setListener(listener);
        }
    }

    private void switchToInfoForm() {
        if (hcInfoForm != null) {
            hcInfoForm.dispose();
        }
        DistributionBean hadoopDistribution = HadoopDistributionsHelper.HADOOP.getDistribution(distributionCombo.getText(), true);
        DistributionVersion hadoopVersion = hadoopDistribution.getVersion(versionCombo.getText(), true);

        if (HCVersionUtil.isHDI(getConnection())) {
            hcInfoForm = new HDIInfoForm(this, connectionItem, existingNamesArray, creation);
        } else if (HCVersionUtil.isSynapse(getConnection())) {
            hcInfoForm = new SynapseInfoForm(this, connectionItem, existingNamesArray, creation, hadoopDistribution,
                    hadoopVersion);
        } else if (HCVersionUtil.isGoogleDataproc(getConnection())) {
            hcInfoForm = new GoogleDataprocInfoForm(this, connectionItem, existingNamesArray, creation, hadoopDistribution,
                    hadoopVersion);
        } else if (HCVersionUtil.isDataBricks(getConnection())) {
            hcInfoForm = new DataBricksInfoForm(this, connectionItem, existingNamesArray, creation, hadoopDistribution,
                    hadoopVersion);
        } else if (HCVersionUtil.isExecutedThroughKnox(getConnection()) && useKnoxButton.getSelection()) {
            hcInfoForm = new KnoxInfoForm(this, connectionItem, existingNamesArray, creation, hadoopDistribution,
                    hadoopVersion);
            // } else if (HCVersionUtil.isCde(getConnection())) {
            // hcInfoForm = new CDEInfoForm(this, connectionItem, existingNamesArray, creation, hadoopDistribution,
            // hadoopVersion);
        } else {
            hcInfoForm = new StandardHCInfoForm(this, connectionItem, existingNamesArray, creation, hadoopDistribution,
                    hadoopVersion);
        }
        hcInfoForm.setReadOnly(readOnly);
        hcInfoForm.setListener(listener);
        hcInfoForm.updateForm();
        hcInfoForm.checkFieldsValue();
        this.layout();
    }

    private void updateVersionPart() {
        DistributionBean distribution = HadoopDistributionsHelper.HADOOP.getDistribution(distributionCombo.getText(), true);
        versionCombo.getCombo().setItems(distribution.getVersionsDisplay());
        final DistributionVersion defaultVersion = distribution.getDefaultVersion();
        DistributionVersion hadoopVersion = distribution.getVersion(getConnection().getDfVersion(), false);
        if (hadoopVersion != null && hadoopVersion.displayVersion != null) {
            versionCombo.setText(hadoopVersion.displayVersion);
        } else if (defaultVersion != null) {
            versionCombo.setText(defaultVersion.displayVersion);
        } else {
            versionCombo.getCombo().select(0);
        }

        if (distribution.useCustom()) {
            versionCombo.setHideWidgets(true);
            hideControl(useYarnButton, false);
            hideControl(customButton, false);
        } else {
            versionCombo.setHideWidgets(false);
            hideControl(useYarnButton, true);
            hideControl(customButton, true);
        }
    }

    private void updateKnoxPart() {
        if(HCVersionUtil.isExecutedThroughKnox(getConnection())) {
            hideControl(useKnoxButton, false);
        } else {
            hideControl(useKnoxButton, true);
        }
    }

    private void updateYarnContent() {
        useYarnButton.setSelection(getConnection().isUseYarn());
    }

    private void updateKnoxContent() {
        boolean isUseKnox = Boolean.getBoolean(getConnection().getParameters().get(ConnParameterKeys.CONN_PARA_KEY_USE_KNOX));
        useKnoxButton.setSelection(isUseKnox);
    }

    @Override
    public boolean checkFieldsValue() {
        if (distributionCombo.getSelectionIndex() == -1) {
            updateStatus(IStatus.ERROR, Messages.getString("HadoopClusterForm.check.distribution")); //$NON-NLS-1$
            return false;
        }

        if (!getConnection().isUseCustomVersion()) {
            if (versionCombo.getSelectionIndex() == -1) {
                updateStatus(IStatus.ERROR, Messages.getString("HadoopClusterForm.check.version")); //$NON-NLS-1$
                return false;
            }
        }

        if (hcInfoForm != null) {
            return hcInfoForm.checkFieldsValue();
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
        if (isReadOnly() != readOnly) {
            adaptFormToReadOnly();
        }
        if (visible) {
            adaptFormToEditable();
            updateStatus(getStatusLevel(), getStatus());
        }
    }
}
