/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.android.settings.backup;

import static com.google.common.truth.Truth.assertThat;

import android.content.Context;
import android.content.Intent;

import com.android.settings.core.BasePreferenceController;
import com.android.settings.testutils.SettingsRobolectricTestRunner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import androidx.preference.Preference;

@RunWith(SettingsRobolectricTestRunner.class)
@Config(shadows = {ShadowPrivacySettingsUtils.class})
public class DataManagementPreferenceControllerTest {
    private final String KEY = "data_management";
    private Context mContext;
    private DataManagementPreferenceController mController;
    private PrivacySettingsConfigData mPSCD;
    private Preference mPreference;
    private String mTitle;

    @Mock
    private Intent mIntent;

    @After
    public void tearDown() {
        ShadowPrivacySettingsUtils.reset();
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mContext = RuntimeEnvironment.application;
        mPSCD = PrivacySettingsConfigData.getInstance();
        mController = new DataManagementPreferenceController(mContext, KEY);
        mPreference = new Preference(mContext);
        mTitle = "Title";
    }

    @Test
    public void updateState_backupEnabled_hadManageIntent_hasManageLable_prefShouldBeHasTitle() {
        mPSCD.setBackupEnabled(true);
        mPSCD.setBackupGray(false);
        mPSCD.setManageIntent(mIntent);
        mPSCD.setManageLabel(mTitle);
        mController.updateState(mPreference);
        assertThat(mPreference.getTitle())
                .isEqualTo(mTitle);
    }

    @Test
    public void getAvailabilityStatus_isAdmin_backupEnabled_hadManageIntent_shouldBeAvailable() {
        ShadowPrivacySettingsUtils.setIsAdminUser(true);
        mPSCD.setBackupEnabled(true);
        mPSCD.setBackupGray(false);
        mPSCD.setManageIntent(mIntent);
        mPSCD.setManageLabel(mTitle);

        assertThat(mController.getAvailabilityStatus())
                .isEqualTo(BasePreferenceController.AVAILABLE);
    }

    @Test
    public void getAvailabilityStatus_isNotAdminUser_shouldBeDisabledForUser() {
        ShadowPrivacySettingsUtils.setIsAdminUser(false);
        assertThat(mController.getAvailabilityStatus())
                .isEqualTo(BasePreferenceController.DISABLED_FOR_USER);
    }

    @Test
    public void
    getAvailabilityStatus_isAdminUser_backupEnabled_nullManageIntent_shouldBeDisabledUnsupported() {
        ShadowPrivacySettingsUtils.setIsAdminUser(true);
        mPSCD.setBackupEnabled(true);
        mPSCD.setBackupGray(false);
        mPSCD.setManageIntent(null);
        mPSCD.setManageLabel(mTitle);

        assertThat(mController.getAvailabilityStatus())
                .isEqualTo(BasePreferenceController.UNSUPPORTED_ON_DEVICE);
    }
}
