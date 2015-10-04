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
package com.hybridbpm.ui.component.bpm.designer;

import com.hybridbpm.model.ProcessModel;
import com.hybridbpm.model.TaskModel;
import com.hybridbpm.model.TaskModel.TASK_PRIORITY;
import com.hybridbpm.model.TransitionModel;
import com.hybridbpm.ui.HybridbpmUI;
import com.hybridbpm.ui.component.ConfigureWindow;
import com.hybridbpm.ui.component.ProcessModelLayoutInterface;
import com.hybridbpm.ui.component.TransitionManager;
import com.hybridbpm.ui.component.bpm.window.TransitionConfigureWindow;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.DragAndDropWrapper.WrapperTargetDetails;
import com.vaadin.ui.DragAndDropWrapper.WrapperTransferable;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.dialogs.ConfirmDialog;

/**
 *
 * @author Marat Gubaidullin
 */
public class ProcessModelLayout extends AbsoluteLayout implements ProcessModelLayoutInterface, Button.ClickListener {

    private ProcessEditor processEditor;
    private ProcessModel processModel;
    private TransitionManager transitionManager;
    private ActiveElement activeElement;
    // active element button bar
    private final Button btnEdit = new Button(null, this);
    private final Button btnDelete = new Button(null, this);
    private final CssLayout elementButtonBar = new CssLayout();
    // process width and heigth button bar
    private final Button btnUp = new Button(null, this);
    private final Button btnDown = new Button(null, this);
    private final Button btnLeft = new Button(null, this);
    private final Button btnRight = new Button(null, this);
    private final VerticalLayout widthButtonBar = new VerticalLayout();
    private final CssLayout heightButtonBar = new CssLayout();

    public DropHandler dropHandler = new ProcessModelLayoutDropHandler(this);

    public void initUI() {
        removeAllComponents();
        addStyleName("process-model");
        setImmediate(true);
        setWidth(processModel.getWidth(), Sizeable.Unit.PIXELS);
        setHeight(processModel.getHeight(), Sizeable.Unit.PIXELS);

        prepareButtonsBars();

        transitionManager = new TransitionManager(this);
        transitionManager.setSizeFull();
        transitionManager.setValue(processModel.getWidth(), processModel.getHeight());
        transitionManager.setValue(processModel.getTaskModels(), processModel.getTransitionModels());
        addComponent(transitionManager);

        for (TaskModel taskModel : processModel.getTaskModels().values()) {
            ElementModelLayout elementModelLayout = new ElementModelLayout(taskModel, this);
            addComponent(elementModelLayout, "left:" + taskModel.getX() + "px; top:" + taskModel.getY() + "px");
        }
        activeElement = new ActiveElement(ActiveElement.TYPE.PROCESS, processModel, null, null);
    }

    @Override
    public void setTransitionElementValue(String transitionModelId, float x, float y) {
        TransitionModel transitionModel = processModel.getTransitionModels().get(transitionModelId);
        transitionModel.setX(x);
        transitionModel.setY(y);
//        processModel.addTransitionModel(transitionModel);
    }

    @Override
    public void addTransitionElement(String beginElementId, String endElementId) {
        StringBuilder name = new StringBuilder("transition");
        int count = 0;
        while (processModel.getTransitionModelByName(name.toString() + count) != null) {
            count++;
        }
        name.append(count);
        Float x;
        Float y;
        if (beginElementId.equals(endElementId)) {
            x = (processModel.getTaskModelById(beginElementId).getX() + processModel.getTaskModelById(beginElementId).getWidth() + 25);
            y = (processModel.getTaskModelById(beginElementId).getY() - 25);
        } else {
            x = (processModel.getTaskModelById(beginElementId).getX() + processModel.getTaskModelById(endElementId).getX()) / 2;
            y = (processModel.getTaskModelById(beginElementId).getY() + processModel.getTaskModelById(endElementId).getY()) / 2;
        }
        processModel.addTransitionModelById(name.toString(), beginElementId, endElementId, x, y);
        transitionManager.setValue(processModel.getTaskModels(), processModel.getTransitionModels());
    }

    public void setTaskActive(TaskModel taskModel) {
        transitionManager.setDragger(taskModel);
        unselectAllElements();
        for (Component comp : this) {
            if (comp instanceof ElementModelLayout) {
                ElementModelLayout eml = ((ElementModelLayout) comp);
                if (eml.getTaskModel().getId().equals(taskModel.getId())) {
                    eml.setStyleSelected(true);
                    activeElement.setTaskModel(taskModel);
                    break;
                }
            }
        }
        activateButtonBar();
    }

    public void removeTaskElement(TaskModel taskModel) {
        for (Component comp : this) {
            if (comp instanceof ElementModelLayout) {
                ElementModelLayout eml = ((ElementModelLayout) comp);
                if (eml.getTaskModel().getId().equals(taskModel.getId())) {
                    removeComponent(comp);
                    break;
                }
            }
        }
    }

    @Override
    public void setTransitionActive(String transitionId) {
        unselectAllElements();
        TransitionModel transitionModel = processModel.getTransitionModelById(transitionId);
        activeElement.setTransitionModel(transitionModel);
        activateButtonBar();
    }

    public void unselectAllElements() {
        removeComponent(elementButtonBar);
        for (Component comp : this) {
            if (comp instanceof ElementModelLayout) {
                ElementModelLayout eml = ((ElementModelLayout) comp);
                eml.setStyleSelected(false);
            }
        }
    }

    @Override
    public void setProcessActive() {
        removeComponent(elementButtonBar);
        for (Component comp : this) {
            if (comp instanceof ElementModelLayout) {
                ElementModelLayout eml = ((ElementModelLayout) comp);
                eml.setStyleSelected(false);
            }
        }
        activeElement.setProcessModel(processModel);
        transitionManager.setValue(processModel.getTaskModels(), processModel.getTransitionModels(), null);
//        setDeleteShortcutListener(false);
    }

    public ProcessModel getProcessModel() {
        return processModel;
    }

    public void setProcessModel(ProcessModel processModel) {
        this.processModel = processModel;
    }

    public void setProcessEditor(ProcessEditor processEditor) {
        this.processEditor = processEditor;
    }

    @Override
    public void addTaskModel(float x, float y) {
        StringBuilder name = new StringBuilder("task");
        int count = 0;
        while (processModel.getTaskModelByName(name.toString() + count) != null) {
            count++;
        }
        name.append(count);
        TaskModel taskModel = new TaskModel(name.toString(), null, TaskModel.TASK_TYPE.HUMAN, TaskModel.GATE_TYPE.EXLUSIVE, TaskModel.GATE_TYPE.EXLUSIVE, TASK_PRIORITY.NORMAL, x, y);
        processModel.getTaskModels().put(taskModel.getId(), taskModel);
        ElementModelLayout elementModelLayout = new ElementModelLayout(taskModel, this);
        addComponent(elementModelLayout, "left:" + taskModel.getX() + "px; top:" + taskModel.getY() + "px");
        addTransitionElement(activeElement.getTaskModel().getId(), taskModel.getId());
        setTaskActive(taskModel);
    }

    public ActiveElement getActiveElement() {
        return activeElement;
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        if (event.getButton().equals(btnUp)) {
            processModel.setHeight(processModel.getHeight() - 100);
            processEditor.setProcessModel(processModel);
        } else if (event.getButton().equals(btnDown)) {
            processModel.setHeight(processModel.getHeight() + 100);
            processEditor.setProcessModel(processModel);
        } else if (event.getButton().equals(btnRight)) {
            processModel.setWidth(processModel.getWidth() + 100);
            processEditor.setProcessModel(processModel);
        } else if (event.getButton().equals(btnLeft)) {
            processModel.setWidth(processModel.getWidth() - 100);
            processEditor.setProcessModel(processModel);
        } else if (event.getButton().equals(btnDelete)) {
            ConfirmDialog cd = ConfirmDialog.show(HybridbpmUI.getCurrent(), "Confirmation", "Delete element?", "Yes", "Cancel", new ConfirmDialog.Listener() {

                @Override
                public void onClose(ConfirmDialog dialog) {
                    if (dialog.isConfirmed()) {
                        if (activeElement.getType().equals(ActiveElement.TYPE.TASK)) {
                            processModel.removeTaskModelByName(activeElement.getTaskModel().getName());
                            removeTaskElement(activeElement.getTaskModel());
                        } else if (activeElement.getType().equals(ActiveElement.TYPE.TRANSITION)) {
                            processModel.removeTransitionModel(activeElement.getTransitionModel().getId());
                        }
                        removeComponent(elementButtonBar);
                        transitionManager.setValue(processModel.getTaskModels(), processModel.getTransitionModels(), null);
                    }
                }
            });
            cd.addStyleName("dialog");
            cd.getCancelButton().addStyleName("small");
            cd.getOkButton().addStyleName("small");
            cd.getOkButton().addStyleName("default");
            cd.getOkButton().focus();

        } else if (event.getButton().equals(btnEdit)) {
            if (activeElement.getType().equals(ActiveElement.TYPE.TRANSITION)) {
                TransitionConfigureWindow tgw = new TransitionConfigureWindow();
                tgw.initUI(this);
                tgw.addCloseListener(new Window.CloseListener() {

                    @Override
                    public void windowClose(Window.CloseEvent e) {
                        processEditor.setProcessModel(processModel);
                    }
                });
                HybridbpmUI.getCurrent().addWindow(tgw);
            } else {
                final TaskConfigureCustomComponent tcl = new TaskConfigureCustomComponent();
                tcl.initUI(this);
                final ConfigureWindow configureWindow = new ConfigureWindow(tcl, "Task");
                configureWindow.setClickListener(new Button.ClickListener() {

                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        if (event.getButton().equals(configureWindow.btnClose)) {
                            configureWindow.close();
                        } else if (event.getButton().equals(configureWindow.btnOk)) {
                            tcl.save();
                        }
                        configureWindow.close();
                    }
                });
                configureWindow.addCloseListener(new Window.CloseListener() {

                    @Override
                    public void windowClose(Window.CloseEvent e) {
                        processEditor.setProcessModel(processModel);
                    }
                });
                configureWindow.setSize(800, 600);
                HybridbpmUI.getCurrent().addWindow(configureWindow);
            }
        }
    }

    private void prepareButtonsBars() {
        // element button bar
        btnEdit.addStyleName(ValoTheme.BUTTON_LINK);
        btnEdit.addStyleName(ValoTheme.BUTTON_SMALL);
        btnEdit.addStyleName("edit-button");
        btnEdit.setIcon(FontAwesome.PENCIL_SQUARE);

        btnDelete.addStyleName(ValoTheme.BUTTON_LINK);
        btnDelete.addStyleName(ValoTheme.BUTTON_SMALL);
        btnDelete.addStyleName("edit-button");
        btnDelete.setIcon(FontAwesome.TIMES);

        elementButtonBar.addComponent(btnEdit);
        elementButtonBar.addComponent(btnDelete);
        elementButtonBar.setId("button-bar");

        // width button bar
        btnRight.setIcon(FontAwesome.ARROW_CIRCLE_RIGHT);
        btnRight.addStyleName(ValoTheme.BUTTON_LINK);

        btnLeft.setIcon(FontAwesome.ARROW_CIRCLE_LEFT);
        btnLeft.addStyleName(ValoTheme.BUTTON_LINK);

        widthButtonBar.addComponent(btnRight);
        widthButtonBar.addComponent(btnLeft);
        widthButtonBar.addStyleName("width-button-bar");
        widthButtonBar.setWidthUndefined();
        addComponent(widthButtonBar);

        // height button bar
        btnUp.setIcon(FontAwesome.ARROW_CIRCLE_UP);
        btnUp.addStyleName(ValoTheme.BUTTON_LINK);

        btnDown.setIcon(FontAwesome.ARROW_CIRCLE_DOWN);
        btnDown.addStyleName(ValoTheme.BUTTON_LINK);

        heightButtonBar.addComponent(btnUp);
        heightButtonBar.addComponent(btnDown);
        heightButtonBar.addStyleName("height-button-bar");
        addComponent(heightButtonBar);
    }

    private void activateButtonBar() {
        if (activeElement.getType().equals(ActiveElement.TYPE.TRANSITION)) {
            TransitionModel transitionModel = activeElement.getTransitionModel();
            addComponent(elementButtonBar, "left:" + (transitionModel.getX() - 15) + "px; top:" + (transitionModel.getY() - 25) + "px");
            elementButtonBar.addStyleName("horizontal");
            elementButtonBar.removeStyleName("vertical");
            btnEdit.focus();
        } else {
            TaskModel taskModel = activeElement.getTaskModel();
            addComponent(elementButtonBar, "left:" + (taskModel.getX() + taskModel.getWidth() + 3) + "px; top:" + (taskModel.getY() + 15) + "px");
            elementButtonBar.addStyleName("vertical");
            elementButtonBar.removeStyleName("horizontal");
        }
    }

    private class ProcessModelLayoutDropHandler implements DropHandler {

        private final ProcessModelLayout processModelLayout;

        public ProcessModelLayoutDropHandler(ProcessModelLayout processModelLayout) {
            this.processModelLayout = processModelLayout;
        }

        @Override
        public void drop(DragAndDropEvent event) {
            WrapperTransferable transfrable = (WrapperTransferable) event.getTransferable();
            WrapperTargetDetails details = (WrapperTargetDetails) event.getTargetDetails();
            System.out.println("transfrable " + transfrable.getClass().getCanonicalName());
            System.out.println("transfrable.getDraggedComponent() " + transfrable.getDraggedComponent().getClass().getCanonicalName());
            if (transfrable.getDraggedComponent() instanceof Button) {
                // Calculate the drag coordinate difference
                int xChange = details.getMouseEvent().getClientX() - transfrable.getMouseDownEvent().getClientX();
                int yChange = details.getMouseEvent().getClientY() - transfrable.getMouseDownEvent().getClientY();

                // Move the component in the absolute layout
                AbsoluteLayout.ComponentPosition componentPosition = processModelLayout.getPosition(transfrable.getSourceComponent());
                componentPosition.setLeftValue(componentPosition.getLeftValue() + xChange);
                componentPosition.setTopValue(componentPosition.getTopValue() + yChange);

                ElementModelLayout elementModelLayout = (ElementModelLayout) ((Button) transfrable.getDraggedComponent()).getParent();
                TaskModel elementModel = elementModelLayout.getTaskModel();
                elementModel.setX(componentPosition.getLeftValue());
                elementModel.setY(componentPosition.getTopValue());
                processModel.getTaskModels().put(elementModel.getId(), elementModel);
                transitionManager.setValue(processModel.getTaskModels(), processModel.getTransitionModels());
                setTaskActive(elementModel);
            }
        }

        @Override
        public AcceptCriterion getAcceptCriterion() {
            return AcceptAll.get();
        }
    }
}
