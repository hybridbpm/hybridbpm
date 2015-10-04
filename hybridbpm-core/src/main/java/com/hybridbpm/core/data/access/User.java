/*
 * Copyright (c) 2011-2015 Marat Gubaidullin. 
 *
 * This file is part of HYBRIDBPM.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 */
package com.hybridbpm.core.data.access;

import com.orientechnologies.orient.core.record.impl.ORecordBytes;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 *
 * @author Marat Gubaidullin
 */
public class User implements Serializable {

    @Id
    protected Object id;

    public static final String SYSTEM = "system";
    public static final String SYSTEM_FIRSTNAME = "System";
    public static final String SYSTEM_LASTNAME = "HybridBPM";
    public static final String ADMINISTRATOR = "administrator";
    public static final String ADMINISTRATOR_FIRSTNAME = "Administrator";
    public static final String ADMINISTRATOR_LASTNAME = "HybridBPM";

    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String password;
    private User manager;
    private String locale;
    private Integer firstVisibleHourOfDay;
    private Integer lastVisibleHourOfDay;
    @OneToOne(orphanRemoval = true)
    private ORecordBytes image;

    private STATUS status;

    public enum STATUS {

        ENABLED, DISABLED
    };
    
    transient private Boolean sync;
    
    public User() {
    }

    public static User getSystemUser() {
        User systemUser = new User();
        systemUser.setUsername(SYSTEM);
        systemUser.setFirstName(SYSTEM_FIRSTNAME);
        systemUser.setLastName(SYSTEM_LASTNAME);
        return systemUser;
    }

    public Object getId() {
        return id;
    }
    
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public User getManager() {
        return manager;
    }

    public void setManager(User manager) {
        this.manager = manager;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public ORecordBytes getImage() {
        return image;
    }

    public void setImage(ORecordBytes image) {
        this.image = image;
    }

    public Integer getFirstVisibleHourOfDay() {
        return firstVisibleHourOfDay;
    }

    public void setFirstVisibleHourOfDay(Integer firstVisibleHourOfDay) {
        this.firstVisibleHourOfDay = firstVisibleHourOfDay;
    }

    public Integer getLastVisibleHourOfDay() {
        return lastVisibleHourOfDay;
    }

    public void setLastVisibleHourOfDay(Integer lastVisibleHourOfDay) {
        this.lastVisibleHourOfDay = lastVisibleHourOfDay;
    }

    public Boolean getSync() {
        return sync;
    }

    public void setSync(Boolean sync) {
        this.sync = sync;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final User other = (User) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }
    
}
