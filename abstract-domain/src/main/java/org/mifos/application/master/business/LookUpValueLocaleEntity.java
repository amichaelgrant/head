/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.application.master.business;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.ejb.QueryHints;

@NamedQueries({
    @NamedQuery(name = "lookUpValueLocaleEntity.getByLocaleAndLookUpId", 
                query = "from LookUpValueLocaleEntity l where l.localeId=:aLocaleId and l.lookUpId=:aLookUpId",
                hints = { @QueryHint(name=QueryHints.HINT_CACHEABLE,value="true") })
})

@Entity
@Table(name = "lookup_value_locale")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class LookUpValueLocaleEntity {
    /*
     * The maximum length of lookUpValue as defined in the SQL schema. This
     * value should track changes in the schema.
     */
    public static final Integer MAX_LOOKUP_VALUE_STRING_LENGTH = 300;
    /**
     * The actual text value that this object represents.
     */
    @Column(name = "lookup_value")
    private String lookUpValue;

    /**
     * The locale object representing the locale for which this object is
     * defined.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "locale_id", unique = true, insertable = false, updatable = false)
    private SupportedLocalesEntity locale;

    /**
     * The id of the LookUpValueEntity that this object is associated with.
     */
    @Column(name = "lookup_id")
    private Integer lookUpId;

    /**
     * The id (primary key) for this object
     */
    @Id
    @GeneratedValue
    @Column(name = "lookup_value_id", nullable = false)
    private Integer lookUpValueId;

    /**
     * The id of the locale for which this object is defined.
     */
    @Column(name = "locale_id")
    private Short localeId;

    public Integer getLookUpValueId() {
        return lookUpValueId;
    }

    public void setLookUpValueId(Integer lookUpValueId) {
        this.lookUpValueId = lookUpValueId;
    }

    public SupportedLocalesEntity getLocale() {
        return locale;
    }

    public void setLocaleId(Short localeId) {
        this.localeId = localeId;
    }

    public Short getLocaleId() {
        return localeId;
    }

    public void setLocale(SupportedLocalesEntity locale) {
        this.locale = locale;
    }

    public Integer getLookUpId() {
        return lookUpId;
    }

    public void setLookUpId(Integer lookUpId) {
        this.lookUpId = lookUpId;
    }

    public String getLookUpValue() {
        return lookUpValue;
    }

    public void setLookUpValue(String lookUpValue) {
        this.lookUpValue = lookUpValue;
    }

    public String getLocaleName() {
        return locale.getLocaleName();
    }
}