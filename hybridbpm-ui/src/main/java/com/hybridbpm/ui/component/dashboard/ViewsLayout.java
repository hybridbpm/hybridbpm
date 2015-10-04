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
package com.hybridbpm.ui.component.dashboard;

import com.hybridbpm.core.data.dashboard.ViewDefinition;
import com.hybridbpm.ui.HybridbpmUI;
import com.hybridbpm.ui.component.AbstractTableLayout;
import com.hybridbpm.ui.component.LinkButton;
import com.hybridbpm.ui.component.TableButton;
import com.hybridbpm.ui.component.TableButtonBar;
import com.vaadin.data.Item;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Table;

@SuppressWarnings("serial")
public class ViewsLayout extends AbstractTableLayout {

    public ViewsLayout() {
        super();
        btnAdd.setCaption("Add view");
        setMargin(new MarginInfo(true, false, false, false));
    }

    @Override
    public void buttonClick(final Button.ClickEvent event) {
        super.buttonClick(event);
        if (event.getButton() instanceof TableButton && ((TableButton)event.getButton()).getType().equals(TableButton.TYPE.EDIT)){
            ViewDefinition viewDefinition = ((TableButton<ViewDefinition>) event.getButton()).getCustomData();
            ViewManager.editViewDefinitionWindow(viewDefinition);
        } else if (event.getButton() instanceof TableButton && ((TableButton)event.getButton()).getType().equals(TableButton.TYPE.DELETE)){
            ViewDefinition viewDefinition = ((TableButton<ViewDefinition>) event.getButton()).getCustomData();
            ViewManager.deleteViewDefinitionWindow(viewDefinition);
        }
    }

    @Override
    public void prepareTable() {
        iTable.addContainerProperty("url", Component.class, null, "URL", null, Table.Align.LEFT);
        iTable.addContainerProperty("title", String.class, null, "Title", null, Table.Align.LEFT);
        iTable.addContainerProperty("order", Integer.class, null, "Order", null, Table.Align.LEFT);
        iTable.addContainerProperty("actions", TableButtonBar.class, null, "Actions", null, Table.Align.LEFT);
        iTable.setColumnWidth("actions", 80);
        iTable.setVisibleColumns("url", "title", "order", "actions");
    }

    @Override
    public void refreshTable() {
        iTable.removeAllItems();

        for (ViewDefinition viewDefinition : HybridbpmUI.getDashboardAPI().getViewDefinitions()) {
            Item item = iTable.addItem(viewDefinition.getId());
            item.getItemProperty("url").setValue(new LinkButton<>(viewDefinition.getUrl(), viewDefinition, this));
            item.getItemProperty("title").setValue(viewDefinition.getTitle().getValue(HybridbpmUI.getCurrent().getLocale()));
            item.getItemProperty("order").setValue(viewDefinition.getOrder());
            item.getItemProperty("actions").setValue(getTableButtonBar(viewDefinition));
        }
        iTable.sort(new Object[]{"order"}, new boolean[]{true});
    }

    private Object getTableButtonBar(ViewDefinition viewDefinition) {
        TableButton editButton = TableButton.createEdit(viewDefinition, this);
        TableButton deleteButton = TableButton.createDownload(viewDefinition, this);
        return new TableButtonBar(editButton, deleteButton);
    }

    @Override
    public void addNew() {
        ViewManager.editViewDefinitionWindow(null);
    }

}
