/* 
 * Copyright (c) 2011-2015 Marat Gubaidullin. 
 * 
 * This file is part of HYBRIDBPM.
 * 
 * HybridBPM is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * HybridBPM is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with HybridBPM.  If not, see <http ://www.gnu.org/licenses/>.
 */
package com.hybridbpm.model;

import java.util.*;

/**
 *
 * @author Marat Gubaidullin
 */
public class ProcessModel extends AbstractModel {

    private Map<String, TaskModel> taskModels;
    private Map<String, TransitionModel> transitionModels;
    private List<FieldModel> variableModels;
    private List<FileModel> fileModels;
    private int width = 800;
    private int height = 600;
    private boolean useCounter = true;
    private long counter = 0;
    private ModelStatus status;

    public ProcessModel() {
    }

    public static ProcessModel createDefault() {
        ProcessModel processModel = new ProcessModel();
        processModel.addHumanTaskModel("task0", "Start process", null, TaskModel.TASK_PRIORITY.NORMAL, TaskModel.GATE_TYPE.EXLUSIVE, TaskModel.GATE_TYPE.EXLUSIVE, 100f, 100f);
        processModel.addVariableModel("var1", String.class.getCanonicalName(), "", FieldModel.EDITOR_TYPE.TEXT_FIELD);
        return processModel;
    }

    public void removeTaskModelByName(String taskName) {
        // find task
        String taskId = null;
        for (TaskModel taskModel : getTaskModels().values()) {
            if (taskModel.getName().equals(taskName)) {
                taskId = taskModel.getId();
            }
        }
        // prepare delete transition list
        ArrayList<String> transToDelete = new ArrayList<>();
        for (TransitionModel trans : transitionModels.values()) {
            if (trans.getBeginTaskModel().equals(taskId) || trans.getEndTaskModel().equals(taskId)) {
                transToDelete.add(trans.getId());
            }
        }

        // delete transitions from tasks
        for (Map.Entry<String, TaskModel> taskModelValue : getTaskModels().entrySet()) {
            TaskModel taskModel = taskModelValue.getValue();
            for (String transId : transToDelete) {
                taskModel.removeTransitionById(transId);
            }
            taskModelValue.setValue(taskModel);
        }

        for (String transId : transToDelete) {
            removeTransitionModel(transId);
        }
        getTaskModels().remove(taskId);
    }

    public TaskModel getTaskModelByName(String taskName) {
        for (TaskModel taskModel : getTaskModels().values()) {
            if (taskModel.getName().equals(taskName)) {
                return taskModel;
            }
        }
        return null;
    }

    public TaskModel getTaskModelById(String taskId) {
        return getTaskModels().get(taskId);
    }

    public Map<String, TransitionModel> getTransitionModels() {
        if (transitionModels == null) {
            transitionModels = new HashMap<>();
        }
        return transitionModels;
    }

    public ProcessModel addTransitionModel(TransitionModel transitionModel) {
        this.getTransitionModels().put(transitionModel.getId(), transitionModel);
        // set outgoing transitions to task
        this.getTaskModels().get(transitionModel.getBeginTaskModel()).getOutgoingTransitionModel().add(transitionModel);
        // set incoming transitions to task
        this.getTaskModels().get(transitionModel.getEndTaskModel()).getIncomingTransitionModel().add(transitionModel);
        return this;
    }

    public TransitionModel getTransitionModelByName(String transitionName) {
        for (TransitionModel transitionModel : getTransitionModels().values()) {
            if (transitionModel.getName().equals(transitionName)) {
                return transitionModel;
            }
        }
        return null;
    }

    public TransitionModel getTransitionModelById(String transitionModelId) {
        return this.getTransitionModels().get(transitionModelId);
    }

    public void removeTransitionModel(String transitionId) {
        // delete transitions from tasks
        for (Map.Entry<String, TaskModel> taskModelValue : getTaskModels().entrySet()) {
            TaskModel taskModel = taskModelValue.getValue();
            taskModel.removeTransitionById(transitionId);
            taskModelValue.setValue(taskModel);
        }

        getTransitionModels().remove(transitionId);
    }

    public void setTransitionModels(Map<String, TransitionModel> transitionModels) {
        this.transitionModels = transitionModels;
    }

    public ProcessModel addTaskModel(TaskModel taskModel) {
        this.getTaskModels().put(taskModel.getId(), taskModel);
        return this;
    }

//    WE SHOULD NOT RENAME TASK BECAUSE USER SEE ONLY TITLE  

    public Map<String, TaskModel> getTaskModels() {
        if (taskModels == null) {
            taskModels = new HashMap<>();
        }
        return taskModels;
    }

    public Map<String, TaskModel> getStartTaskModels() {
        Map<String, TaskModel> result = new HashMap<>();
        for (TaskModel tm : taskModels.values()) {
            if (tm.getIncomingTransitionModel().isEmpty()) {
                result.put(tm.getName(), tm);
            }
        }
        return result;
    }

    public void setTaskModels(Map<String, TaskModel> taskModels) {
        this.taskModels = taskModels;
    }

    public ProcessModel addHumanTaskModel(String name, String title, String role, TaskModel.GATE_TYPE joinType, TaskModel.GATE_TYPE splitType, Float x, Float y) {
        return addTaskModel(new TaskModel(name, title, TaskModel.TASK_TYPE.HUMAN, joinType, splitType, role, TaskModel.TASK_PRIORITY.NORMAL, x, y));
    }

    public ProcessModel addHumanTaskModel(String name, String title, String role, TaskModel.TASK_PRIORITY taskPriority, TaskModel.GATE_TYPE joinType, TaskModel.GATE_TYPE splitType, Float x, Float y) {
        return addTaskModel(new TaskModel(name, title, TaskModel.TASK_TYPE.HUMAN, joinType, splitType, role, taskPriority, x, y));
    }

    public ProcessModel addAutomaticTaskModel(String name, String title, String connector, TaskModel.GATE_TYPE joinType, TaskModel.GATE_TYPE splitType, Float x, Float y) {
        return addTaskModel(new TaskModel(name, title, TaskModel.TASK_TYPE.AUTOMATIC, joinType, splitType, connector, x, y));
    }

    public ProcessModel addTransitionModel(String name, String beginTaskName, String endTaskName, Float x, Float y) {
        TaskModel beginTaskModel = getTaskModelByName(beginTaskName);
        TaskModel endTaskModel = getTaskModelByName(endTaskName);
        return addTransitionModel(TransitionModel.createTransitionModel(name, beginTaskModel.getId(), endTaskModel.getId(), x, y));
    }

    public ProcessModel addTransitionModelById(String name, String beginTaskModelId, String endTaskModelId, Float x, Float y) {
        return addTransitionModel(TransitionModel.createTransitionModel(name, beginTaskModelId, endTaskModelId, x, y));
    }

    public ProcessModel addDefaultTransitionModel(String name, String beginTaskName, String endTaskName, Float x, Float y) {
        TaskModel beginTaskModel = getTaskModelByName(beginTaskName);
        TaskModel endTaskModel = getTaskModelByName(endTaskName);
        return addTransitionModel(TransitionModel.createDefaultTransitionModel(name, beginTaskModel.getId(), endTaskModel.getId(), x, y));
    }

    public ProcessModel addConditionalTransitionModel(String name, String beginTaskName, String endTaskName, String expression, Float x, Float y) {
        TaskModel beginTaskModel = getTaskModelByName(beginTaskName);
        TaskModel endTaskModel = getTaskModelByName(endTaskName);
        return addTransitionModel(TransitionModel.createConditionalTransitionModel(name, beginTaskModel.getId(), endTaskModel.getId(), expression, x, y));
    }

    public List<FieldModel> getVariableModels() {
        if (variableModels == null) {
            variableModels = new ArrayList<>();
        }
        return variableModels;
    }

    public void setVariableModels(List<FieldModel> variableModels) {
        this.variableModels = variableModels;
    }

    public FieldModel getVariableModelByName(String variableName) {
        for (FieldModel variableModel : getVariableModels()) {
            if (variableModel.getName().equals(variableName)) {
                return variableModel;
            }
        }
        return null;
    }

    public FieldModel getFieldModelByName(String variableName) {
        FieldModel result = getVariableModelByName(variableName);
        return (result);
    }

    public ProcessModel addVariableModel(String name, String className, String defaultValue, FieldModel.EDITOR_TYPE editor) {
        this.getVariableModels().add(new FieldModel(name, null, className, defaultValue, FieldModel.COLLECTION_TYPE.NONE, editor));
        return this;
    }

    public ProcessModel addFileModel(String name, String description, boolean multiple) {
        this.getFileModels().add(new FileModel(name, description, multiple));
        return this;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isUseCounter() {
        return useCounter;
    }

    public void setUseCounter(boolean useCounter) {
        this.useCounter = useCounter;
    }

    public long getCounter() {
        return counter;
    }

    public void setCounter(long counter) {
        this.counter = counter;
    }

    public ModelStatus getStatus() {
        return status;
    }

    public void setStatus(ModelStatus status) {
        this.status = status;
    }

    public List<FileModel> getFileModels() {
        if (fileModels == null){
            fileModels = new ArrayList<>();
        }
        return fileModels;
    }

    public void setFileModels(List<FileModel> fileModels) {
        this.fileModels = fileModels;
    }
    
    public FileModel getFileModelByName(String name) {
        for (FileModel fileModel : getFileModels()) {
            if (fileModel.getName().equals(name)) {
                return fileModel;
            }
        }
        return null;
    }

}
