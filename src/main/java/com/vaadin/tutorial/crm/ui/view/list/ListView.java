package com.vaadin.tutorial.crm.ui.view.list;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.tutorial.crm.backend.entity.Contact;
import com.vaadin.tutorial.crm.backend.service.CompanyService;
import com.vaadin.tutorial.crm.backend.service.ContactService;
import com.vaadin.tutorial.crm.ui.MainLayout;


@Route(value="", layout = MainLayout.class)
@PageTitle("Contacts | Data")
public class ListView extends VerticalLayout {

    private ContactService contactService;

    private Grid<Contact> grid = new Grid<>(Contact.class);
    private TextField filterText = new TextField();
    private ContactForm contactForm;

    public ListView(ContactService contactService,
                    CompanyService companyService) {
        this.contactService = contactService;
        addClassName("list-view");
        setSizeFull();

        configureGrid();

        contactForm = new ContactForm(companyService.findAll());
        contactForm.addListener(ContactForm.SaveEvent.class, this::saveContact);
        contactForm.addListener(ContactForm.DeleteEvent.class, this::deleteContact);
        contactForm.addListener(ContactForm.CloseEvent.class, e -> this.closeEditor());
        closeEditor();

        Div content = new Div(grid, contactForm);
        content.addClassName("content");
        content.setSizeFull();

        add(getToolbar(), content);


        updateList();

    }

    private HorizontalLayout getToolbar() {


        filterText.setPlaceholder("Filter by name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        Button addContactButton = new Button("Add contact");
        addContactButton.addClickListener(click -> addContact());

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addContactButton);

        toolbar.addClassName("toolbar");
        return toolbar;
    }
    void addContact(){
        grid.asSingleSelect().clear();
        editContact(new Contact());
    }

    private void saveContact(ContactForm.SaveEvent event) {
        contactService.save(event.getContact());
        updateList();
        closeEditor();
    }

    private void deleteContact(ContactForm.DeleteEvent event) {
        contactService.delete(event.getContact());
        updateList();
        closeEditor();
    }

    private void configureGrid(){
        grid.addClassName("contact-grid");
        grid.setSizeFull();
        grid.setColumns("firstName", "lastName", "email", "status");

        grid.asSingleSelect().addValueChangeListener(event ->
                editContact(event.getValue()));

        grid.asSingleSelect().addValueChangeListener(event ->

                editContact(event.getValue()));
    }
    public void editContact(Contact contact) {

        if (contact == null) {
            closeEditor();
        } else {
            contactForm.setContact(contact);
            contactForm.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        contactForm.setContact(null);
        contactForm.setVisible(false);
        removeClassName("editing");
    }

    private void configureFilter(){
        filterText.setPlaceholder("Filter by name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());
    }
    private void updateList(){
        grid.setItems(contactService.findAll(filterText.getValue()));
    }

}
