//Copyright (c) 2011-2015 Marat Gubaidullin. 
//
//This file is part of HYBRIDBPM.
//
//Licensed under the Apache License, Version 2.0 (the "License"); you may not
//use this file except in compliance with the License. You may obtain a copy of
//the License at
//
//http://www.apache.org/licenses/LICENSE-2.0
//
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
//WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
//License for the specific language governing permissions and limitations under
//the License.

function htextcomplete(textareaId, users, projects) {
    $(textareaId).textcomplete([
        {// html
            mentions: users,
            match: /\B@(\w*)$/,
            search: function (term, callback) {
                callback($.map(this.mentions, function (mention) {
                    return mention.indexOf(term) === 0 ? mention : null;
                }));
            },
            index: 1,
            replace: function (mention) {
                return '@' + mention + ' ';
            }
        },
        {// html
            mentions: projects,
            match: /\B#(\w*)$/,
            search: function (term, callback) {
                callback($.map(this.mentions, function (mention) {
                    return mention.indexOf(term) === 0 ? mention : null;
                }));
            },
            index: 1,
            replace: function (mention) {
                return '#' + mention + ' ';
            }
        }
    ], {appendTo: 'body'}).overlay([
        {
            match: /\B@\w+/g,
            css: {
                'color': '#FF9800'
            }
        },
        {
            match: /\B#\w+/g,
            css: {
                'color': '#FF9800'
            }
        }
    ]);
}
;