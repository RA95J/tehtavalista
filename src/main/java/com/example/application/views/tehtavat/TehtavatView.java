package com.example.application.views.tehtavat;

import com.example.application.data.Task;
import com.example.application.services.TaskService;
import com.example.application.utils.MessageProvider;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.vaadin.lineawesome.LineAwesomeIconUrl;


import java.util.Locale;
import java.util.Optional;


@AnonymousAllowed
@Route(value = "/:tehtavatID?/:action?(edit)")
@PageTitle("Tehtävät")
@CssImport("./themes/tehtavalista/views/tehtavat-view.css")
@RouteAlias("")
@Menu(order = 1, icon = LineAwesomeIconUrl.BOOK_OPEN_SOLID)
public class TehtavatView extends Div implements BeforeEnterObserver {

    private final TaskService taskService;
    private final Grid<Task> grid = new Grid<>(Task.class, false);
    private final BeanValidationBinder<Task> binder = new BeanValidationBinder<>(Task.class);
    private Task task;

    private final TextField titleFilter = new TextField();
    private final TextField personFilter = new TextField();
    private final TextField commentFilter = new TextField();
    private final TextField categoryFilter = new TextField();

    private final TextField title = new TextField();
    private final TextField category = new TextField();
    private final TextField comment = new TextField();
    private final TextField person = new TextField();

    private final Button searchButton = new Button();
    private final Button clearFiltersButton = new Button();


    private final Button save = new Button();
    private final Button cancel = new Button();

    private final Button languageButton = new Button();

    private HorizontalLayout filterActions;

    public TehtavatView(TaskService taskService) {
        this.taskService = taskService;

        setDefaultLocaleIfNotSet();
        buildLayout();
        updateTexts();
    }

    private void setDefaultLocaleIfNotSet() {
        if (VaadinSession.getCurrent().getAttribute(Locale.class) == null) {
            VaadinSession.getCurrent().setAttribute(Locale.class, new Locale("fi"));
            Locale.setDefault(new Locale("fi"));
        } else {
            Locale.setDefault(VaadinSession.getCurrent().getAttribute(Locale.class));
        }
    }

    private void buildLayout() {
        addClassName("tehtavat-view");
        setSizeFull();
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(true);
        layout.setSpacing(true);

        title.addClassNames(LumoUtility.BorderRadius.LARGE, LumoUtility.Padding.SMALL);
        category.addClassNames(LumoUtility.BorderRadius.LARGE, LumoUtility.Padding.SMALL);
        comment.addClassNames(LumoUtility.BorderRadius.LARGE, LumoUtility.Padding.SMALL);
        person.addClassNames(LumoUtility.BorderRadius.LARGE, LumoUtility.Padding.SMALL);

        titleFilter.addClassNames(LumoUtility.BorderRadius.LARGE, LumoUtility.Padding.SMALL);
        personFilter.addClassNames(LumoUtility.BorderRadius.MEDIUM, LumoUtility.Padding.SMALL);
        commentFilter.addClassNames(LumoUtility.BorderRadius.SMALL, LumoUtility.Padding.SMALL);
        categoryFilter.addClassNames(LumoUtility.BorderRadius.FULL, LumoUtility.Padding.SMALL);

        HorizontalLayout filters = new HorizontalLayout(titleFilter, personFilter, commentFilter, categoryFilter);
        titleFilter.addValueChangeListener(e -> refreshGrid());
        personFilter.addValueChangeListener(e -> refreshGrid());
        commentFilter.addValueChangeListener(e -> refreshGrid());
        categoryFilter.addValueChangeListener(e -> refreshGrid());

        searchButton.addClickListener(e -> refreshGrid());
        searchButton.addClassName("highlighted-button");

        clearFiltersButton.addClickListener(e -> clearFilters());
        clearFiltersButton.getStyle().set("background-color", "lightblue");

        filterActions = new HorizontalLayout(searchButton, clearFiltersButton);

        setupLanguageButton();
        layout.add(languageButton, filters, filterActions);

        grid.addColumn(Task::getTask).setKey("task");
        grid.addColumn(t -> t.getCategory() != null ? t.getCategory().getName() : "").setKey("category");
        grid.addColumn(t -> t.getComment() != null ? t.getComment().getComment() : "").setKey("comment");
        grid.addColumn(Task::getPersonNames).setKey("persons");
        grid.addComponentColumn(item -> {
            Button delete = new Button(MessageProvider.get("button.delete"), e -> openDeleteDialog(item));
            delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
            return delete;
        }).setKey("actions");

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeight("300px");
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                populateForm(event.getValue());
            } else {
                clearForm();
            }
        });

        FormLayout formLayout = new FormLayout();

        // Lisätään kentät kahteen sarakkeeseen
        formLayout.addFormItem(title, MessageProvider.get("task.grid.header.task"));
        formLayout.addFormItem(comment, MessageProvider.get("task.grid.header.comment"));
        formLayout.addFormItem(category, MessageProvider.get("task.grid.header.category"));
        formLayout.addFormItem(person, MessageProvider.get("task.grid.header.persons"));

        // Määritellään 2 saraketta
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 2)
        );

        // Kentille ei tarvitse tässä erikseen colspania
        HorizontalLayout buttons = new HorizontalLayout(save, cancel);

        binder.forField(title)
                .asRequired("Tehtävän nimi on pakollinen")
                .withValidator(name -> name.length() <= 255, "Tehtävän nimi saa olla enintään 255 merkkiä")
                .bind(Task::getTask, Task::setTask);

        binder.forField(category)
                .asRequired("Kategoria on pakollinen")
                .withValidator(name -> name.length() <= 255, "Kategorian nimi saa olla enintään 255 merkkiä")
                .bind(
                        t -> t.getCategory() != null ? t.getCategory().getName() : "",
                        (t, val) -> {
                            if (t.getCategory() == null) t.setCategory(new com.example.application.data.Category());
                            t.getCategory().setName(val);
                        });

        binder.forField(comment)
                .asRequired("Kommentti on pakollinen")
                .withValidator(comment -> comment.length() <= 255, "Kommentti saa olla enintään 255 merkkiä")
                .bind(
                        t -> t.getComment() != null ? t.getComment().getComment() : "",
                        (t, val) -> {
                            if (t.getComment() == null) t.setComment(new com.example.application.data.Comment());
                            t.getComment().setComment(val);
                        });

        binder.forField(person)
                .asRequired("Henkilön nimi on pakollinen")
                .withValidator(name -> name.length() <= 255, "Henkilön nimi saa olla enintään 255 merkkiä")
                .bind(
                        t -> t.getPersons().stream().findFirst().map(p -> p.getName()).orElse(""),
                        (t, val) -> {
                            if (t.getPersons().isEmpty()) {
                                com.example.application.data.Person p = new com.example.application.data.Person();
                                p.setName(val);
                                t.getPersons().add(p);
                            } else {
                                t.getPersons().iterator().next().setName(val);
                            }
                        });


        cancel.addClickListener(e -> {
            clearForm();
            grid.deselectAll();
        });

        save.addClickListener(e -> {
            try {
                if (this.task == null || this.task.getId() == null) {
                    this.task = new Task();
                }
                binder.writeBean(this.task);
                taskService.save(this.task);
                clearForm();
                refreshGrid();
                Notification.show("Tallennettu onnistuneesti");
            } catch (ObjectOptimisticLockingFailureException ex) {
                Notification.show("Tallennus epäonnistui: samanaikainen muokkaus", 3000, Notification.Position.MIDDLE);
            } catch (ValidationException e1) {
                Notification.show("Tarkista kentät", 3000, Notification.Position.MIDDLE);
            }
        });

        layout.add(filters, grid, formLayout, buttons);
        add(layout);
        refreshGrid();
    }

    private void clearFilters() {
        titleFilter.clear();
        personFilter.clear();
        commentFilter.clear();
        categoryFilter.clear();
        refreshGrid();
    }

    private void setupLanguageButton() {
        languageButton.addClickListener(e -> {
            switchLanguage();
            updateTexts();
        });
    }

    private void switchLanguage() {
        Locale current = Locale.getDefault();
        Locale newLocale = current.getLanguage().equals("fi") ? Locale.ENGLISH : new Locale("fi");
        VaadinSession.getCurrent().setAttribute(Locale.class, newLocale);
        Locale.setDefault(newLocale);
    }

    private void updateTexts() {
        titleFilter.setLabel(MessageProvider.get("filter.title"));
        personFilter.setLabel(MessageProvider.get("filter.person"));
        commentFilter.setLabel(MessageProvider.get("filter.comment"));
        categoryFilter.setLabel(MessageProvider.get("filter.category"));

        title.setLabel(MessageProvider.get("task.grid.header.task"));
        category.setLabel(MessageProvider.get("task.grid.header.category"));
        comment.setLabel(MessageProvider.get("task.grid.header.comment"));
        person.setLabel(MessageProvider.get("task.grid.header.persons"));

        save.setText(MessageProvider.get("button.save"));
        cancel.setText(MessageProvider.get("button.cancel"));

        searchButton.setText(MessageProvider.get("button.filter"));
        clearFiltersButton.setText(MessageProvider.get("button.clear-filters"));

        grid.getColumnByKey("task").setHeader(MessageProvider.get("task.grid.header.task"));
        grid.getColumnByKey("category").setHeader(MessageProvider.get("task.grid.header.category"));
        grid.getColumnByKey("comment").setHeader(MessageProvider.get("task.grid.header.comment"));
        grid.getColumnByKey("persons").setHeader(MessageProvider.get("task.grid.header.persons"));
        grid.getColumnByKey("actions").setHeader(MessageProvider.get("task.grid.header.actions"));

        languageButton.setText(getLanguageButtonText());
    }

    private String getLanguageButtonText() {
        return "fi".equals(Locale.getDefault().getLanguage()) ? "In English" : "Suomeksi";
    }

    private void refreshGrid() {
        if ((titleFilter.getValue() == null || titleFilter.getValue().isEmpty())
                && (personFilter.getValue() == null || personFilter.getValue().isEmpty())
                && (commentFilter.getValue() == null || commentFilter.getValue().isEmpty())
                && (categoryFilter.getValue() == null || categoryFilter.getValue().isEmpty())) {
            grid.setItems(taskService.list());
        } else {
            grid.setItems(taskService.search(titleFilter.getValue(), personFilter.getValue(), commentFilter.getValue(), categoryFilter.getValue()));
        }
    }

    private void clearForm() {
        task = new Task();
        binder.readBean(task);
    }

    private void populateForm(Task task) {
        this.task = task;
        binder.readBean(this.task);
    }

    private void setupButtons() {
        searchButton.addClickListener(e -> refreshGrid());
        searchButton.addClassName("highlighted-button");

        clearFiltersButton.addClickListener(e -> clearFilters());
        clearFiltersButton.getStyle().set("background-color", "lightblue");
    }


    private void openDeleteDialog(Task taskToDelete) {
        Dialog dialog = new Dialog();
        dialog.add(MessageProvider.get("dialog.delete.confirmation") + ": " + taskToDelete.getTask());
        Button yes = new Button(MessageProvider.get("button.yes"), e -> {
            taskService.delete(taskToDelete.getId());
            refreshGrid();
            clearForm();
            dialog.close();
        });
        Button no = new Button(MessageProvider.get("button.cancel"), e -> dialog.close());
        dialog.add(new HorizontalLayout(yes, no));
        dialog.open();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        refreshGrid();
        Optional<Long> id = event.getRouteParameters().get("tehtavatID").map(Long::parseLong);
        id.ifPresent(taskId -> {
            taskService.get(taskId).ifPresentOrElse(
                    this::populateForm,
                    () -> {
                        Notification.show("Tehtävää ei löytynyt");
                        event.forwardTo(TehtavatView.class);
                    }
            );
        });
    }
}