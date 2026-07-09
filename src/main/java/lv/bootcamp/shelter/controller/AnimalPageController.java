package lv.bootcamp.shelter.controller;


import lv.bootcamp.shelter.form.AnimalForm;
import lv.bootcamp.shelter.model.AnimalType;
import lv.bootcamp.shelter.service.AnimalService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AnimalPageController
{

    private final AnimalService animalService;

    //index page
    @GetMapping("/")
    public String index()
    {
        return "index";
    }

    //all animals
    @GetMapping("/animals")
    public String listAnimals(Model model)
    {
        model.addAttribute("animals", animalService.findAll());
        return "animals";
    }

    //new animal form (only admin)
    @GetMapping("/animals/new")
    public String newAnimalForm(Model model)
    {
        model.addAttribute("form", new AnimalForm(null, null, null, null, null, null));
        model.addAttribute("types", AnimalType.values());
        return "animals-new";
    }

    //add animal after form submit
    @PostMapping("/animals")
    public String createAnimal(AnimalForm form)
    {
        animalService.createFromForm(form);
        return "redirect:/animals";
    }
}