package yourdiet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AjoutAgendaController {

    @GetMapping("/ajouter-repas")
    public String afficherFormulaireAjoutAgenda() {
        return "ajoutAgenda";
    }
}
