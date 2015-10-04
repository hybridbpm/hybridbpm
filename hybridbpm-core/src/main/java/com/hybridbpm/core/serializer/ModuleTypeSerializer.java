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
package com.hybridbpm.core.serializer;

import com.hybridbpm.core.util.HybridbpmCoreUtil;
import com.hybridbpm.core.data.development.Module;
import com.hybridbpm.model.Translated;
import com.orientechnologies.orient.core.serialization.serializer.object.OObjectSerializer;

/**
 *
 * @author Marat Gubaidullin
 */
public class ModuleTypeSerializer implements OObjectSerializer<Module.MODULE_TYPE, String> {

    @Override
    public Object serializeFieldValue(Class<?> iClass, Module.MODULE_TYPE moduleType) {
        return moduleType.name();
    }

    @Override
    public Object unserializeFieldValue(Class<?> iClass, String moduleType) {
        return Module.MODULE_TYPE.valueOf(moduleType);
    }

    
   
    
}
