package com.example.application.views.user;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@RolesAllowed("USER")
@PageTitle("Käyttäjän sivu")
@Route("user")
@Menu(order = 3, icon = LineAwesomeIconUrl.USER_SOLID)
public class UserView extends Composite<VerticalLayout> {

    public UserView() {
        getContent().add(new H1("Tervetuloa Käyttäjän Sivulle"));
    }
}
