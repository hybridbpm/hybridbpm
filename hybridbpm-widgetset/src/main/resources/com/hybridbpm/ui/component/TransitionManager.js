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

com_hybridbpm_ui_component_TransitionManager = function () {

    // Create the component
    var mycomponent = new TransitionManagerComponent(this.getElement(), this.getState().processWidth, this.getState().processHeight);
    // Handle changes from the server-side
    this.onStateChange = function () {
        mycomponent.setValue(this.getState().taskModels, this.getState().transitionModels);
        mycomponent.setDragger(this.getState().taskModel);
    };
    // Pass user interaction to the server-side
    var connector = this;
    mycomponent.changePosition = function (circle) {
        connector.changePosition(circle.data("id"), circle.attr("cx"), circle.attr("cy"));
    };
    mycomponent.addTransition = function (drager) {
        connector.addTransition(drager.data("beginTaskModel").id, drager.data("endTaskModel").id);
    };
    mycomponent.addTask = function (drager) {
        connector.addTask(drager.attr("cx"), drager.attr("cy"));
    };
    mycomponent.setTransitionActive = function (transition) {
        connector.setTransitionActive(transition);
    };
    mycomponent.setProcessActive = function () {
        connector.setProcessActive();
    };
};

TransitionManagerComponent = function (element, width, height) {
    this.element = element;
    var component = this;
    var arrowStyle = {
        "stroke": "#343536",
        "width": 1,
        "stroke-width": 1.5,
        "arrow-end": "block-wide-long",
        "arrow-start": "none"
    };
    var noArrowStyle = {
        "stroke": "#343536",
        "width": 1,
        "stroke-width": 1.5
    };
    var labelStyle = {
        "font-weight": "normal",
        "font-family": "'Open Sans', Arial, sans-serif",
        "font-size": "11px",
        "fill": "#1a1a1a",
        "stroke": "#ececec",
        "stroke-width": "0.1px"
    };
    var processSelected = true;
    var paper = Raphael(element, width, height);
    element.childNodes[0].onclick = function () {
        if (processSelected) {
            component.setProcessActive();
        }
        processSelected = true;
    };

    var drager = paper.circle(0, 0, 0);
    drager.attr("fill", "transparent").attr("stroke", "transparent");
    var text = paper.text(0, 0, '\uf061');
    text.attr("fill", "transparent").attr("stroke", "transparent");

    this.setValue = function (taskModels, transitionModels) {
        paper.clear();
        var elements = {};
        var element_paths = {};
        for (i = 0; i < taskModels.length; i++) {
            var step = taskModels[i];
            var task_element = paper.path([["M", step.x, step.y], ["L", step.x + step.width, step.y], ["L", step.x + step.width, step.y + step.height], ["L", step.x, step.y + step.height], ["L", step.x, step.y], "z"]);
            task_element.data("id", step.id);
            task_element.attr("stroke", "transparent");
            elements[step.id] = step;
            element_paths[step.id] = task_element;
        }

        var getTaskModel = function (id) {
            for (i = 0; i < taskModels.length; i++) {
                var step = taskModels[i];
                if (step.id === id) {
                    return step;
                }
            }
            return null;
        };

        var drawtransition = function (startx, starty, cx, cy, finishx, finishy, startPath, finishPath, self) {
            var firstPath = (cx < startx && cx < finishx) ? ["Q", cx, starty, cx, cy] : ["Q", startx, cy, cx, cy];
            var secondPath = (cx > startx && cx > finishx) ? ["Q", cx, finishy, finishx, finishy] : ["Q", finishx, cy, finishx, finishy];
            var pathVars = [["M", startx, starty], firstPath, secondPath];

            if (self === true) {
                // up
                if (cy < starty - 25 && cx < startx - 50) {
                    firstPath = ["Q", startx + 25, cy, cx, cy];
                    secondPath = ["Q", cx, starty - 20, startx - 50, starty - 20];
                    pathVars = [["M", startx, starty - 25], firstPath, secondPath];
                } else if (cy < starty - 25 && cx > startx - 50 && cx < startx) {
                    firstPath = ["Q", startx, cy, cx, cy];
                    secondPath = ["Q", startx - 50, cy, startx - 45, starty - 25];
                    pathVars = [["M", startx, starty - 25], firstPath, secondPath];
                } else if (cy < starty - 25 && cx > startx && cx < startx + 50) {
                    firstPath = ["Q", startx, cy, cx, cy];
                    secondPath = ["Q", startx + 50, cy, startx + 45, starty - 25];
                    pathVars = [["M", startx, starty - 25], firstPath, secondPath];
                } else if (cy < starty - 25 && cx > startx + 50) {
                    firstPath = ["Q", startx - 25, cy, cx, cy];
                    secondPath = ["Q", cx, starty - 20, startx + 50, starty - 20];
                    pathVars = [["M", startx, starty - 25], firstPath, secondPath];
                }// down 
                else if (cy > starty + 25 && cx < startx - 50) {
                    firstPath = ["Q", startx + 25, cy, cx, cy];
                    secondPath = ["Q", cx, starty + 20, startx - 50, starty + 20];
                    pathVars = [["M", startx, starty + 25], firstPath, secondPath];
                } else if (cy > starty + 25 && cx > startx - 50 && cx < startx) {
                    firstPath = ["Q", startx, cy, cx, cy];
                    secondPath = ["Q", startx - 50, cy, startx - 45, starty + 25];
                    pathVars = [["M", startx, starty + 25], firstPath, secondPath];
                } else if (cy > starty + 25 && cx > startx && cx < startx + 50) {
                    firstPath = ["Q", startx, cy, cx, cy];
                    secondPath = ["Q", startx + 50, cy, startx + 45, starty + 25];
                    pathVars = [["M", startx, starty + 25], firstPath, secondPath];
                } else if (cy > starty + 25 && cx > startx + 50) {
                    firstPath = ["Q", startx - 25, cy, cx, cy];
                    secondPath = ["Q", cx, starty + 20, startx + 50, starty + 20];
                    pathVars = [["M", startx, starty + 25], firstPath, secondPath];
                }// left 
                else if (cy > starty - 25 && cy < starty + 25 && cx < startx - 50) {
                    firstPath = ["Q", cx, starty - 20, cx, cy];
                    secondPath = ["Q", cx, starty + 20, startx - 50, starty + 20];
                    pathVars = [["M", startx - 50, starty - 20], firstPath, secondPath];
                }// right 
                else if (cy > starty - 25 && cy < starty + 25 && cx > startx + 50) {
                    firstPath = ["Q", cx, starty + 20, cx, cy];
                    secondPath = ["Q", cx, starty - 20, startx + 50, starty - 20];
                    pathVars = [["M", startx + 50, starty + 20], firstPath, secondPath];
                }
            } else {
                var intersection1 = Raphael.pathIntersection(pathVars.toString(), startPath);
                var intersection2 = Raphael.pathIntersection(pathVars.toString(), finishPath);
                firstPath = (cx < startx && cx < finishx) ? ["Q", cx, intersection1[0].y, cx, cy] : ["Q", intersection1[0].x, cy, cx, cy];
                secondPath = (cx > startx && cx > finishx) ? ["Q", cx, intersection2[0].y, intersection2[0].x, intersection2[0].y] : ["Q", intersection2[0].x, cy, intersection2[0].x, intersection2[0].y];
                pathVars = [["M", intersection1[0].x, intersection1[0].y], firstPath, secondPath];
            }
            Raphael.path2curve(pathVars);
            var path = paper.path(pathVars);
            path.attr(arrowStyle);
            return path;
        };

        var trans_elements = {};
        for (i = 0; i < transitionModels.length; i++) {
            var transition = transitionModels[i];
//            console.log(transition);
            var circle = paper.circle(transition.x, transition.y, 20);
            circle.attr("stroke", "transparent");
            circle.data("id", transition.id);
            circle.data("beginTaskModel", transition.beginTaskModel);
            circle.data("endTaskModel", transition.endTaskModel);
            var label = paper.text(transition.x, transition.y + 5, transition.name);
            label.data("id", transition.id);
            label.attr(labelStyle);
            circle.pair = label;
            label.pair = circle;
            var movers = paper.set().push(circle).push(label).attr({
                cursor: "pointer"
            });

            var startx = elements[transition.beginTaskModel].x + elements[transition.beginTaskModel].width / 2;
            var starty = elements[transition.beginTaskModel].y + elements[transition.beginTaskModel].height / 2;
            var finishx = elements[transition.endTaskModel].x + elements[transition.endTaskModel].width / 2;
            var finishy = elements[transition.endTaskModel].y + elements[transition.endTaskModel].height / 2;
            var path = drawtransition(startx, starty, circle.attr("cx"), circle.attr("cy"), finishx, finishy, element_paths[transition.beginTaskModel].attrs.path.toString(), element_paths[transition.endTaskModel].attrs.path.toString()
                    , transition.beginTaskModel === transition.endTaskModel);
            trans_elements[transition.id] = {
                "circle": circle,
                "path": path,
                "startx": startx,
                "starty": starty,
                "finishx": finishx,
                "finishy": finishy
            };
        }

        var setTransitionTextNormal = function () {
            for (trans_element in trans_elements) {
                trans_elements[trans_element].circle.pair.attr("font-weight", "normal");
            }
        };

        var start = function () {
            processSelected = false;
            // storing original coordinates
            var attx = this.type === "circle" ? this.attr("cx") : this.attr("x");
            var atty = this.type === "circle" ? this.attr("cy") : this.attr("y");
            this.ox = attx;
            this.oy = atty;
            attx = this.pair.type === "circle" ? this.pair.attr("cx") : this.pair.attr("x");
            atty = this.pair.type === "circle" ? this.pair.attr("cy") : this.pair.attr("y");
            this.pair.ox = attx;
            this.pair.oy = atty;
            setTransitionTextNormal();
            var label = this.type === "circle" ? this.pair : this;
            label.attr("font-weight", "bold");
            drager.remove();
            text.remove();
            component.setTransitionActive(label.data("id"));
            var buttonBar = document.getElementById("button-bar");
            buttonBar.style.display = "none";
        };
        var move = function (dx, dy) {
            // Move main element
            var circle = this.type === "circle" ? this : this.pair;
            var label = this.type === "circle" ? this.pair : this;
            circle.attr({
                cx: circle.ox + dx,
                cy: circle.oy + dy
            });
            label.attr({
                x: label.ox + dx,
                y: label.oy + dy
            });
            var te = trans_elements[circle.data("id")];
            var path = drawtransition(te.startx, te.starty, circle.ox + dx, circle.oy + dy, te.finishx, te.finishy,
                    element_paths[circle.data("beginTaskModel")].attrs.path.toString(),
                    element_paths[circle.data("endTaskModel")].attrs.path.toString(),
                    circle.data("beginTaskModel") === circle.data("endTaskModel"));
            te.path.remove();
            trans_elements[circle.data("id")] = {
                "circle": circle,
                "path": path,
                "startx": te.startx,
                "starty": te.starty,
                "finishx": te.finishx,
                "finishy": te.finishy
            };
        };
        var up = function () {
            component.changePosition(this.type === "circle" ? this : this.pair);
            var label = this.type === "circle" ? this.pair : this;
            component.setTransitionActive(label.data("id"));
            var buttonBar = document.getElementById("button-bar");
            buttonBar.style.display = "inherit";
        };
        for (trans_element in trans_elements) {
            trans_elements[trans_element].circle.drag(move, start, up);
            trans_elements[trans_element].circle.pair.drag(move, start, up);
        }

        var startDrager = function () {
            processSelected = false;
            this.ox = this.attr("cx");
            this.oy = this.attr("cy");
            this.attr("fill", "transparent").attr("stroke", "transparent");
            this.pair.attr("fill", "transparent").attr("stroke", "transparent");
        };
        var moveDrager = function (dx, dy) {
            this.attr({
                cx: this.ox + dx,
                cy: this.oy + dy
            });
            var startPointX = this.data("beginTaskModel").x + this.data("beginTaskModel").width / 2;
            var startPointY = this.data("beginTaskModel").y + this.data("beginTaskModel").height / 2;
            try {
                var transform = [["M", startPointX, startPointY], ["L", this.attr("cx"), this.attr("cy")]];
                var path = this.data("linepath");
                if (path === undefined) {
                    path = paper.path(transform);
                    path.attr(noArrowStyle);
                } else {
                    path.attr({
                        path: transform
                    });
                }
                // if pointer covered beginTaskModel
                var element = element_paths[this.data("beginTaskModel").id];
                var bbox = element.getBBox(true);
                if (this.data("beginTaskModel").type === 'TASK' && Raphael.isPointInsideBBox(bbox, this.attr("cx"), this.attr("cy"))) {
                    this.data("endTaskModel", getTaskModel(element.data("id")));
                    path = drawtransition(startPointX, startPointY, startPointX + 75, startPointY - 50, startPointX, startPointY,
                            element_paths[this.data("beginTaskModel").id].attrs.path.toString(),
                            element_paths[this.data("beginTaskModel").id].attrs.path.toString(),
                            true);
                } else {
                    this.data("endTaskModel", undefined);
                    for (var i in element_paths) {
                        var element = element_paths[i];
                        var bbox = element.getBBox(true);
                        if (Raphael.isPointInsideBBox(bbox, this.attr("cx"), this.attr("cy"))) {
                            var intersection = Raphael.pathIntersection(transform, element.attrs.path.toString());
                            for (i = 0; i < intersection.length; i++) {
                                if (intersection[0] !== null) {
                                    this.data("endTaskModel", getTaskModel(element.data("id")));
                                    transform = [["M", startPointX, startPointY], ["L", intersection[0].x, intersection[0].y]];
                                    path.attr({
                                        path: transform
                                    });
                                    path.attr(arrowStyle);
                                }
                            }
                        }
                    }
                }
                this.data("linepath", path);
            } catch (err) {
            }

        };
        var upDrager = function () {
            if (this.data("endTaskModel") !== undefined) {
                component.addTransition(this);
                processSelected = true;
            } else {
//                console.log("create new task");
//                console.log(this.attr("cx"));
                component.addTask(this);
            }
            if (this.data("linepath") !== undefined) {
                this.data("linepath").remove();
            }
            this.remove();
        };

        this.setDragger = function (taskModel) {
//            console.log("---------------------------------------setDragger " + taskModel.x + " " + taskModel.y);
            if (drager !== undefined) {
                drager.remove();
                text.remove();
            }
            if (taskModel !== undefined && taskModel !== null && taskModel.x !== undefined && taskModel.y !== undefined) {
                drager = paper.circle(taskModel.x + taskModel.width + 11, taskModel.y + 5, 10);
                drager.data("beginTaskModel", taskModel);
                drager.attr("fill", "transparent").attr("stroke", "#FF9800");
                drager.node.setAttribute("class", "drager");
                drager.drag(moveDrager, startDrager, upDrager);

                text = paper.text(taskModel.x + taskModel.width + 12, taskModel.y + 3, '\uf061');
                text.attr('font-size', 15);
                text.attr('fill', '#FF9800');
                text.attr('font-family', 'FontAwesome');
                drager.pair = text;
                text.pair = drager;

                text.toBack();
                drager.toFront();
            }
        };
    };
};
