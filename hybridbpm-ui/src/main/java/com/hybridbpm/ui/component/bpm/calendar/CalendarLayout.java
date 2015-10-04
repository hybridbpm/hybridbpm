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
package com.hybridbpm.ui.component.bpm.calendar;

import com.hybridbpm.model.Translated;
import com.hybridbpm.core.data.bpm.Task;
import com.hybridbpm.core.data.development.Module;
import com.hybridbpm.ui.HybridbpmUI;
import com.hybridbpm.ui.component.AbstractTabLayout;
import com.hybridbpm.ui.component.bpm.TaskLayout;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.Calendar;
import com.vaadin.ui.Notification;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.components.calendar.CalendarComponentEvents;
import com.vaadin.ui.components.calendar.handler.BasicDateClickHandler;
import com.vaadin.ui.components.calendar.handler.BasicEventMoveHandler;
import com.vaadin.ui.components.calendar.handler.BasicWeekClickHandler;
import com.vaadin.ui.themes.ValoTheme;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("serial")
public class CalendarLayout extends AbstractTabLayout implements Property.ValueChangeListener {

    private static final Logger logger = Logger.getLogger(CalendarLayout.class.getSimpleName());

    private TabSheet tabSheet;
    private Calendar calendar;
    private BeanItemContainer<TaskEvent> calendarContainer;
    private final OptionGroup calendarOptionGroup = new OptionGroup();

    private enum TYPE {

        TODAY, WEEK, MONTH;
    }

    public CalendarLayout() {
        super();
        btnAdd.setCaption("Add task");
        btnAdd.setVisible(false);
        tools.addComponent(calendarOptionGroup, 1);
        setMargin(new MarginInfo(true, false, false, false));
        addComponent(calendar);
        setExpandRatio(calendar, 1f);
        calendarOptionGroup.addItem(TYPE.TODAY);
        calendarOptionGroup.setItemCaption(TYPE.TODAY, "Today");
        calendarOptionGroup.addItem(TYPE.WEEK);
        calendarOptionGroup.setItemCaption(TYPE.WEEK, "Week");
        calendarOptionGroup.addItem(TYPE.MONTH);
        calendarOptionGroup.setItemCaption(TYPE.MONTH, "Month");
        calendarOptionGroup.addValueChangeListener(this);
        calendarOptionGroup.setValue(TYPE.WEEK);
        calendarOptionGroup.setStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
        calendarOptionGroup.addStyleName("small");
    }

    @Override
    public void buttonClick(final Button.ClickEvent event) {
        try {
            if (event.getButton().equals(btnAdd)) {
                addNew();
            } else if (event.getButton().equals(btnRefresh)) {
                refreshData();
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            Notification.show("Error", ex.getMessage(), Notification.Type.ERROR_MESSAGE);
        }
    }

    @Override
    public void prepareUI() {
        calendarContainer = new BeanItemContainer<>(TaskEvent.class);
        calendar = new Calendar();
        calendar.setContainerDataSource(calendarContainer, "caption", "description", "start", "end", "styleName");
        calendar.setSizeFull();
        calendar.setImmediate(true);
        calendar.setLocale(HybridbpmUI.getCurrent().getLocale());
        calendar.setTimeFormat(Calendar.TimeFormat.Format24H);
        

        calendar.setHandler(new BasicEventMoveHandler() {
            @Override
            public void eventMove(CalendarComponentEvents.MoveEvent event) {
                super.eventMove(event);
                if (event.getCalendarEvent() instanceof TaskEvent) {
                    Task task = ((TaskEvent) event.getCalendarEvent()).getTaskInstance();
                    task.setScheduleStartDate(event.getCalendarEvent().getStart());
                    task.setScheduleEndDate(event.getCalendarEvent().getEnd());
                    HybridbpmUI.getBpmAPI().saveTask(task, null, null, null);
                }
            }

        });

        calendar.setHandler(new BasicWeekClickHandler() {

            @Override
            public void weekClick(final CalendarComponentEvents.WeekClick event) {
                calendarOptionGroup.setValue(TYPE.WEEK);
                setWeekView(event.getWeek(), event.getYear());
            }

        });

        calendar.setHandler(new CalendarComponentEvents.EventClickHandler() {
            @Override
            public void eventClick(final CalendarComponentEvents.EventClick event) {
                if (event.getCalendarEvent() instanceof TaskEvent) {
                    Task task = ((TaskEvent) event.getCalendarEvent()).getTaskInstance();
                    TabSheet.Tab tab = tabSheet.addTab(new TaskLayout(task.getId().toString(), task.getProcessModelName(), task.getTaskName(), true), task.getTaskTitle());
                    tab.setClosable(true);
                    tabSheet.setSelectedTab(tab);
                }
            }
        });

        calendar.setHandler(new BasicDateClickHandler() {
            @Override
            public void dateClick(final CalendarComponentEvents.DateClickEvent event) {
                setDayView(event.getDate());
            }
        });

        calendar.setHandler(new CalendarComponentEvents.RangeSelectHandler() {
            @Override
            public void rangeSelect(final CalendarComponentEvents.RangeSelectEvent event) {
                System.out.println(event.getComponent().isMonthlyMode());
                setDayView(event.getStart());
            }
        });
    }

    @Override
    public void refreshData() {
        calendar.setFirstVisibleHourOfDay(HybridbpmUI.getUser().getFirstVisibleHourOfDay());
        calendar.setLastVisibleHourOfDay(HybridbpmUI.getUser().getLastVisibleHourOfDay());
        calendarContainer.removeAllItems();

        for (Task task : HybridbpmUI.getBpmAPI().getMyTasksToDo(HybridbpmUI.getCurrent().getLocale())) {
            if (task.getScheduleStartDate() != null && task.getScheduleEndDate() != null) {
                calendarContainer.addBean(new TaskEvent(task, task.getTaskTitle(), task.getDescription(), task.getScheduleStartDate(), task.getScheduleEndDate()));
            }
        }
    }

    @Override
    public void addNew() {

    }

    public void setTabSheet(TabSheet tabSheet) {
        this.tabSheet = tabSheet;
    }

    private void setDayView(Date date) {
        calendar.setStartDate(date);
        calendar.setEndDate(date);
    }

    private void setWeekView(int week, int year) {
        GregorianCalendar gc = new GregorianCalendar(HybridbpmUI.getCurrent().getLocale());
        gc.set(GregorianCalendar.YEAR, year);
        gc.set(GregorianCalendar.WEEK_OF_YEAR, week);

        // starting at the beginning of the week
        gc.set(GregorianCalendar.DAY_OF_WEEK, gc.getFirstDayOfWeek());
        Date start = gc.getTime();

        // ending at the end of the week
        gc.add(GregorianCalendar.DATE, 6);
        Date end = gc.getTime();

        calendar.setStartDate(start);
        calendar.setEndDate(end);
    }

    private void setMonthView() {
        GregorianCalendar gc = new GregorianCalendar(HybridbpmUI.getCurrent().getLocale());
        gc.setTime(new Date());
        gc.set(GregorianCalendar.HOUR_OF_DAY, 0);
        gc.set(GregorianCalendar.MINUTE, 0);
        gc.set(GregorianCalendar.SECOND, 0);
        gc.set(GregorianCalendar.MILLISECOND, 0);
        gc.set(GregorianCalendar.DAY_OF_MONTH, 1);
        calendar.setStartDate(gc.getTime());
        gc.add(GregorianCalendar.MONTH, 1);
        gc.add(GregorianCalendar.DATE, -1);
        calendar.setEndDate(gc.getTime());
    }

    @Override
    public void valueChange(Property.ValueChangeEvent event) {
        if (Objects.equals(TYPE.TODAY, event.getProperty().getValue())) {
            setDayView(new Date());
        } else if (Objects.equals(TYPE.WEEK, event.getProperty().getValue())) {
            GregorianCalendar gc = new GregorianCalendar(HybridbpmUI.getCurrent().getLocale());
            gc.setTime(new Date());
            setWeekView(gc.get(GregorianCalendar.WEEK_OF_YEAR), gc.get(GregorianCalendar.YEAR));
        } else if (Objects.equals(TYPE.MONTH, event.getProperty().getValue())) {
            setMonthView();
        }
    }

}
