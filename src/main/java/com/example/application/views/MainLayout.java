package com.example.application.views;

import com.example.application.data.Role;
import com.example.application.data.User;
import com.example.application.security.AuthenticatedUser;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.server.menu.MenuEntry;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Layout
@AnonymousAllowed
public class MainLayout extends AppLayout {

    private final AuthenticatedUser authenticatedUser;
    private H1 viewTitle;

    public MainLayout(AuthenticatedUser authenticatedUser) {
        this.authenticatedUser = authenticatedUser;

        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H1();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        addToNavbar(true, toggle, viewTitle);
    }

    private void addDrawerContent() {
        Span appName = new Span("Tehtävälista");
        appName.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.LARGE);
        Header header = new Header(appName);

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();

        List<MenuEntry> menuEntries = MenuConfiguration.getMenuEntries();
        Optional<User> maybeUser = authenticatedUser.get();

        if (maybeUser.isPresent()) {
            Set<Role> roles = maybeUser.get().getRoles();

            for (MenuEntry entry : menuEntries) {
                // Näytetään Adminin sivu vain adminille
                if (entry.title().equalsIgnoreCase("Adminin etusivu") && roles.contains(Role.ADMIN)) {
                    nav.addItem(new SideNavItem(entry.title(), entry.path()));
                }
                // Näytetään Userin sivu vain userille
                else if (entry.title().equalsIgnoreCase("Käyttäjän etusivu") && roles.contains(Role.USER)) {
                    nav.addItem(new SideNavItem(entry.title(), entry.path()));
                }
                // Muut näkyy kaikille
                else if (!entry.title().equalsIgnoreCase("Adminin etusivu") && !entry.title().equalsIgnoreCase("Käyttäjän etusivu")) {
                    nav.addItem(new SideNavItem(entry.title(), entry.path()));
                }
            }
        }

        return nav;
    }

    private Footer createFooter() {
        Footer layout = new Footer();

        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            Button logoutButton = new Button("Log out");
            logoutButton.addClickListener(e -> {
                authenticatedUser.logout();
                getUI().ifPresent(ui -> ui.navigate("login"));
            });
            layout.add(logoutButton);
        } else {
            Anchor loginLink = new Anchor("login", "Sign in");
            layout.add(loginLink);
        }
        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        return MenuConfiguration.getPageHeader(getContent()).orElse("");
    }
}
