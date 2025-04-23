package com.example.application.views.tehtavat;

import com.example.application.data.Tehtavalista;
import com.example.application.services.TehtavalistaService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import java.util.Optional;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Tehtavat")
@Route("tehtavat/:tehtavalistaID?/:action?(edit)")
@Menu(order = 0, icon = LineAwesomeIconUrl.BOOK_OPEN_SOLID)
public class TehtavatView extends Div implements BeforeEnterObserver {

    private final String TEHTAVALISTA_ID = "tehtavalistaID";
    private final String TEHTAVALISTA_EDIT_ROUTE_TEMPLATE = "tehtavat/%s/edit";

    private final Grid<Tehtavalista> grid = new Grid<>(Tehtavalista.class, false);

    private TextField kategoria;
    private TextField nimi;
    private TextField tehtava;
    private TextField kommentti;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<Tehtavalista> binder;

    private Tehtavalista tehtavalista;

    private final TehtavalistaService tehtavalistaService;

    public TehtavatView(TehtavalistaService tehtavalistaService) {
        this.tehtavalistaService = tehtavalistaService;
        addClassNames("tehtavat-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("kategoria").setAutoWidth(true);
        grid.addColumn("nimi").setAutoWidth(true);
        grid.addColumn("tehtava").setAutoWidth(true);
        grid.addColumn("kommentti").setAutoWidth(true);
        grid.setItems(query -> tehtavalistaService.list(VaadinSpringDataHelpers.toSpringPageRequest(query)).stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(TEHTAVALISTA_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(TehtavatView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Tehtavalista.class);

        // Bind fields. This is where you'd define e.g. validation rules

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.tehtavalista == null) {
                    this.tehtavalista = new Tehtavalista();
                }
                binder.writeBean(this.tehtavalista);
                tehtavalistaService.save(this.tehtavalista);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(TehtavatView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("Failed to update the data. Check again that all values are valid");
            }
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> tehtavalistaId = event.getRouteParameters().get(TEHTAVALISTA_ID).map(Long::parseLong);
        if (tehtavalistaId.isPresent()) {
            Optional<Tehtavalista> tehtavalistaFromBackend = tehtavalistaService.get(tehtavalistaId.get());
            if (tehtavalistaFromBackend.isPresent()) {
                populateForm(tehtavalistaFromBackend.get());
            } else {
                Notification.show(
                        String.format("The requested tehtavalista was not found, ID = %s", tehtavalistaId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(TehtavatView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        kategoria = new TextField("Kategoria");
        nimi = new TextField("Nimi");
        tehtava = new TextField("Tehtava");
        kommentti = new TextField("Kommentti");
        formLayout.add(kategoria, nimi, tehtava, kommentti);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Tehtavalista value) {
        this.tehtavalista = value;
        binder.readBean(this.tehtavalista);

    }
}
