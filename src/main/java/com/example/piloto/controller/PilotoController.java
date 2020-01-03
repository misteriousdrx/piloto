package com.example.piloto.controller;

import com.example.piloto.model.Estrategia;
import com.example.piloto.repository.EstrategiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.function.Predicate;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
public class PilotoController {

    @Autowired
    EstrategiaRepository estrategiaRepository;

    List<Map<String, String>> whitelist = new ArrayList<>();
    List<Map<String, String>> blacklist = new ArrayList<>();


    public PilotoController() {

        Map<String, String> massa1 = new HashMap<>();
        Map<String, String> massa2 = new HashMap<>();
        Map<String, String> massa3 = new HashMap<>();
        Map<String, String> massa4 = new HashMap<>();
        Map<String, String> massa5 = new HashMap<>();
        Map<String, String> massa6 = new HashMap<>();
        Map<String, String> massa7 = new HashMap<>();


        massa1.put("agencia", "1500");
        massa1.put("conta", "05201");
        massa1.put("dac", "2");

        massa2.put("agencia", "1500");
        massa2.put("conta", "01010");
        massa2.put("dac", "1");

        massa3.put("agencia", "8064");
        massa3.put("conta", "19199");
        massa3.put("dac", "5");

        massa4.put("agencia", "7063");
        massa4.put("conta", "19199");
        massa4.put("dac", "8");

        massa5.put("agencia", "1500");
        massa5.put("conta", "12345");
        massa5.put("dac", "6");

        massa6.put("agencia", "0000");
        massa6.put("conta", "09876");
        massa6.put("dac", "5");

        massa7.put("agencia", "1500");
        massa7.put("conta", "01010");
        massa7.put("dac", "1");

        whitelist.add(massa1);
        whitelist.add(massa2);
        whitelist.add(massa3);
        whitelist.add(massa4);
        whitelist.add(massa5);

        blacklist.add(massa3);
        blacklist.add(massa6);
        blacklist.add(massa7);

    }

    @RequestMapping(method = GET, path = "/piloto")
    public ResponseEntity<?> getPiloto(@RequestParam Map<String,String> allRequestParams) {

        boolean containsWhitelist = whitelist.contains(allRequestParams);

        boolean containsBlacklist = blacklist.contains(allRequestParams);

        boolean isPilot = (containsWhitelist && !containsBlacklist);

        return ResponseEntity.ok("It works! " + isPilot);

    }

    @RequestMapping(method = GET, path = "/piloto/{idPiloto}")
    public ResponseEntity<?> getPilotoById(@PathVariable String idPiloto) {

        Optional<Estrategia> estrategia = estrategiaRepository.findById(idPiloto);

        if (estrategia.isPresent()) {

            return ResponseEntity.ok(estrategia.get());

        } else {

            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        }

    }

    @RequestMapping(method = GET, path = "/piloto/{idPiloto}/piloto")
    public ResponseEntity<?> getPilotoById(@PathVariable String idPiloto, @RequestParam Map<String,String> allRequestParams) {

        Optional<Estrategia> estrategia = estrategiaRepository.findById(idPiloto);

        if (estrategia.isPresent()) {

            return ResponseEntity.ok(isPiloto(allRequestParams, estrategia));

        } else {

            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        }

    }

    @RequestMapping(method = GET, path = "/piloto/{idPiloto}/pilotos")
    public ResponseEntity<?> getPilotoByIdAndRule(@PathVariable String idPiloto, @RequestParam Map<String,String> allRequestParams) {

        Optional<Estrategia> estrategia = estrategiaRepository.findById(idPiloto);
        HashMap<String, String> agencia = new HashMap<String, String>() {{ put("agencia", Optional.ofNullable(allRequestParams.get("agencia")).orElse("")); }};

        if (estrategia.isPresent()) {

            return ResponseEntity.ok(isPiloto(estrategia, piloto -> {
                return !piloto.getBlacklist().contains(allRequestParams) && piloto.getWhitelist().contains(agencia);
            }));

        } else {

            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        }

    }

    @RequestMapping(method = POST, path = "/piloto")
    public ResponseEntity<?> addPiloto() {

        Estrategia estrategia = new Estrategia();

//        List<Map<String, String>> list = new ArrayList<>();
//
//        HashMap<String, String> map = new HashMap<String, String>() {{
//            put("agencia", "1500");
//        }};
//
//        list.add(map);

        estrategia.setNome("Peloto");
        estrategia.setDescricao("desc");
        estrategia.setDataCriacao(new Date());
//        estrategia.setWhitelist(list);
        estrategia.setWhitelist(whitelist);
        estrategia.setBlacklist(blacklist);

        return ResponseEntity.ok(estrategiaRepository.save(estrategia));


    }

    private boolean isPiloto(@RequestParam Map<String, String> allRequestParams, Optional<Estrategia> estrategia) {
        return estrategia.filter(piloto -> !piloto.getBlacklist().contains(allRequestParams) && piloto.getWhitelist().contains(allRequestParams)).isPresent();
    }


    private boolean isPiloto(Optional<Estrategia> estrategia, Predicate<Estrategia> predicate) {

        Objects.requireNonNull(predicate, "Predicado deve ser informado");

        return predicate.test(estrategia.get());

    }

}
