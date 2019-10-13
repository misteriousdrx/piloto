package com.example.piloto.repository;


import com.example.piloto.model.Estrategia;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
//import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.CrudRepository;


@EnableScan
public interface EstrategiaRepository extends CrudRepository<Estrategia, String> {
//public interface EstrategiaRepository extends MongoRepository<Estrategia, String> {


}
