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
package com.hybridbpm.core.util;

import com.hybridbpm.model.FileModel;

/**
 *
 * @author Marat Gubaidullin
 */
public class FileModelUtil {

    public static String getFileComponent(FileModel fileModel) {
        return "    private FileManager " + " " + fileModel.getName() + "FileManager;";
    }

    public static String getFormDesignElement(FileModel fileModel) {
        return "        <h-file-manager _id=\"" + fileModel.getName() + "FileManager\" caption=\"" + fileModel.getDescription() + "\" />";
    }

}
