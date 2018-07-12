package org.launchcode.controllers;

import org.launchcode.models.Category;
import org.launchcode.models.Cheese;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.CategoryDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;


import javax.validation.Valid;
import java.util.*;

/**
 * Created by LaunchCode
 */
@Controller
@RequestMapping("cheese")
public class CheeseController {

    @Autowired
    private CheeseDao cheeseDao;

    @Autowired
    private CategoryDao categoryDao;

    // Request path: /cheese
    @RequestMapping(value = "")
    public String index(Model model) {

        model.addAttribute("cheeses", cheeseDao.findAll());
        model.addAttribute("title", "My Cheeses");

        return "cheese/index";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String displayAddCheeseForm(Model model) {
        model.addAttribute("title", "Add Cheese");
        model.addAttribute(new Cheese());
        model.addAttribute("categories", categoryDao.findAll());
        return "cheese/add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String processAddCheeseForm(@ModelAttribute  @Valid Cheese newCheese,
                                       Errors errors, @RequestParam int categoryId, Model model) {

        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Cheese");
            return "cheese/add";
        }

        Category cat = categoryDao.findOne(categoryId);
        newCheese.setCategory(cat);

        cheeseDao.save(newCheese);
        return "redirect:";
    }

    @RequestMapping(value = "remove", method = RequestMethod.GET)
    public String displayRemoveCheeseForm(Model model) {
        model.addAttribute("cheeses", cheeseDao.findAll());
        model.addAttribute("title", "Remove Cheese");
        return "cheese/remove";
    }

    @RequestMapping(value = "remove", method = RequestMethod.POST)
    public String processRemoveCheeseForm(@RequestParam int[] cheeseIds) {

        for (int cheeseId : cheeseIds) {
            cheeseDao.delete(cheeseId);
        }

        return "redirect:";
    }

    @RequestMapping(value = "category/{categoryId}", method=RequestMethod.GET)
    public String displayByCategory(Model model, @PathVariable int categoryId) {

        Category category = categoryDao.findOne(categoryId);
        Iterable<Cheese> allCheeses = cheeseDao.findAll();
        List<Cheese> cheeses = new ArrayList<>();

        for (Cheese cheese : allCheeses) {
            Category cat = cheese.getCategory();
            if (cat.getId() == categoryId) {
                cheeses.add(cheese);
            }
        }

        model.addAttribute("cheeses", cheeses);
        model.addAttribute("title", String.format("Cheese in category %s", category.getName()));

        return "cheese/index";

    }
    @RequestMapping(value = "edit/{cheeseId}", method=RequestMethod.GET)
    public String displayEditForm(Model model, @PathVariable int cheeseId) {

        Cheese cheese = cheeseDao.findOne(cheeseId);
        model.addAttribute("cheese", cheese);
        model.addAttribute("title", String.format("Edit Cheese: %s(id=%d)", cheese.getName(), cheeseId));
        model.addAttribute("categories", categoryDao.findAll());

        return "cheese/edit";

    }

    @RequestMapping(value = "edit/{cheeseId}", method=RequestMethod.POST)
    public String processEditForm(int cheeseId, String name, String description, int categoryId) {

        Cheese cheese = cheeseDao.findOne(cheeseId);
        Category cat = categoryDao.findOne(categoryId);
        cheese.setName(name);
        cheese.setDescription(description);
        cheese.setCategory(cat);

        cheeseDao.save(cheese);

        return "redirect:/cheese";

    }

}
