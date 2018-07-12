package org.launchcode.controllers;

import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.PathVariable;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping(value = "menu")
public class MenuController {

    @Autowired
    private MenuDao menuDao;

    @Autowired
    private CheeseDao cheeseDao;

    @RequestMapping(value = "")
    public String index(Model model) {
        model.addAttribute("menus", menuDao.findAll());
        model.addAttribute("title", "Menus");

        return "menu/index";
    }

    @RequestMapping(value = "add", method=RequestMethod.GET)
    public String displayAdd(Model model) {

        model.addAttribute("title", "Add Menu");
        model.addAttribute(new Menu());

        return "menu/add";

    }

    @RequestMapping(value="add", method=RequestMethod.POST)
    public String processAdd(Model model, @ModelAttribute @Valid Menu newMenu, Errors errors) {

        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Menu");
            return "menu/add";
        }

        menuDao.save(newMenu);
        return "redirect:view/" + newMenu.getId();

    }

    @RequestMapping(value = "view/{menuId}", method=RequestMethod.GET)
    public String viewMenu(@PathVariable int menuId, Model model) {

        model.addAttribute("menu", menuDao.findOne(menuId));
        return "menu/view";
    }

    @RequestMapping(value = "add-item/{menuId}", method=RequestMethod.GET)
    public String addItemDisplay(Model model, @PathVariable int menuId) {

        AddMenuItemForm addMenuItemForm = new AddMenuItemForm();
        addMenuItemForm.setMenuId(menuId);
        Menu newMenu = new Menu();
        newMenu = menuDao.findOne(menuId);
        addMenuItemForm.setMenu(newMenu);
        addMenuItemForm.setCheeseId(0);
        addMenuItemForm.setCheeses(cheeseDao.findAll());
        model.addAttribute("form", addMenuItemForm);
        model.addAttribute(new Menu());
        model.addAttribute("title", String.format("Add item to menu: %s", newMenu.getName()));
        return "menu/add-item";

    }

   @RequestMapping(value = "add-item/{menuId}", method=RequestMethod.POST)
    public String addItemProcess(@ModelAttribute  @Valid AddMenuItemForm newAddMenu, Model model, Errors errors) {

        Menu menu = menuDao.findOne(newAddMenu.getMenuId());

        if (errors.hasErrors()) {
            model.addAttribute("title", String.format("Add item to menu: %s", menu.getName()));
            return String.format("redirect:/menu/add-item/%d", newAddMenu.getMenuId());
        }

        Cheese newCheese = cheeseDao.findOne(newAddMenu.getCheeseId());
        menu.addItem(newCheese);

        menuDao.save(menu);
        model.addAttribute("menu", menu);

        return String.format("redirect:/menu/view/%d", newAddMenu.getMenuId());

    }

}
