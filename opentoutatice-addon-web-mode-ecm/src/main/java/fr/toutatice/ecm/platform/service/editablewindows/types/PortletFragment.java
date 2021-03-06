/*
 * (C) Copyright 2014 Académie de Rennes (http://www.ac-rennes.fr/), OSIVIA (http://www.osivia.com) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 *
 * Contributors:
 *   mberhaut1
 *    
 */
package fr.toutatice.ecm.platform.service.editablewindows.types;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;

import fr.toutatice.ecm.platform.service.editablewindows.EwServiceException;
import fr.toutatice.ecm.platform.service.fragments.configuration.ConfigurationBeanHelper;
import fr.toutatice.ecm.platform.service.fragments.configuration.ConfigurationConstants;

/**
 * 
 * Service dédié aux listes
 * 
 */
public class PortletFragment implements EditableWindow {

    private static final String PORTLET_SCHEMA = "portlet_fragments";

    private static final String PORTLET_PROPERTIES = "portlet_properties";


    @Override
    public String prepareCreation(DocumentModel doc, String uri, String region, String belowUri, String code2) throws EwServiceException {

        try {

            // fragments portlet
            Map<String, Object> schPortlets = doc.getProperties(PORTLET_SCHEMA);

            Collection<Object> values = schPortlets.values();

            Object liste = values.iterator().next();

            if (liste instanceof List) {
                List<Map<String, String>> listeData = (List<Map<String, String>>) liste;

                Map<String, String> newEntry = new HashMap<String, String>();

                newEntry.put("refURI", uri);
                newEntry.put("portletInstance", code2);

                listeData.add(newEntry);

                doc.setProperties(PORTLET_SCHEMA, schPortlets);
            }


            // portlet properties
            Map<String, Object> portletProperties = doc.getProperties(PORTLET_PROPERTIES);

            values = portletProperties.values();

            liste = values.iterator().next();

            if (liste instanceof List) {
                List<Map<String, String>> listeData = (List<Map<String, String>>) liste;

                ConfigurationBeanHelper configBean = ConfigurationBeanHelper.getBean();
                List<Map<String, String>> options = configBean.getFragmentOptionsByCode(doc, code2);

                if (options != null) {
                    for (Map<String, String> option : options) {
                        Map<String, String> newEntry = new HashMap<String, String>();

                        newEntry.put("refURI", uri);
                        newEntry.put("key", option.get(ConfigurationConstants.KEY_OPTION));
                        newEntry.put("value", option.get(ConfigurationConstants.VALUE_OPTION));
                        newEntry.put("editable", option.get(ConfigurationConstants.EDITABLE_OPTION));
                        newEntry.put("label", option.get(ConfigurationConstants.LABEL_OPTION));

                        listeData.add(newEntry);
                    }

                    doc.setProperties(PORTLET_PROPERTIES, portletProperties);
                }
            }


        } catch (NuxeoException e) {
            throw new EwServiceException(e);
        }
        return uri;
    }

}
