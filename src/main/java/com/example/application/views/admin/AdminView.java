package com.example.application.views.admin;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@RolesAllowed("ADMIN")
@PageTitle("Adminin sivu")
@Route("admin")
@Menu(order = 2, icon = LineAwesomeIconUrl.USER_SHIELD_SOLID)
public class AdminView extends Composite<VerticalLayout> {

    public AdminView() {
        getContent().add(new H1("Tervetuloa Adminin sivulle"));
    }
}
