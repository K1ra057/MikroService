package com.example.WebDeliverySQL;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.library.Architectures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.Entity;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;
/*
@autrhor Дима
@project MikroService-main
@class ArchitectureTests
@version 1.0.0
@sinc 19.04.2025 - 21 - 32
*/
public class ArchitectureTests {

    private JavaClasses classes;

    @BeforeEach
    void setup() {
        classes = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("com.example.WebDeliverySQL");
    }

    @Test
    void shouldRespectLayeredArchitecture() {
        Architectures.LayeredArchitecture architecture = Architectures.layeredArchitecture()
                .consideringAllDependencies()
                .layer("Controller").definedBy("com.example.WebDeliverySQL.controller..")
                .layer("Service").definedBy("com.example.WebDeliverySQL.service..")
                .layer("Repository").definedBy("com.example.WebDeliverySQL.repository..")
                .layer("Model").definedBy("com.example.WebDeliverySQL.model..")
                .layer("Mapper").definedBy("com.example.WebDeliverySQL.mapper..");

        architecture.whereLayer("Controller").mayNotBeAccessedByAnyLayer();
        architecture.whereLayer("Service").mayOnlyBeAccessedByLayers("Controller");
        architecture.whereLayer("Repository").mayOnlyBeAccessedByLayers("Service");
        architecture.whereLayer("Model").mayOnlyBeAccessedByLayers("Repository", "Service", "Mapper");
        architecture.whereLayer("Mapper").mayOnlyBeAccessedByLayers("Service");

        architecture.check(classes);
    }

    @Test
    void controllersShouldHaveRestControllerAnnotation() {
        classes()
                .that().resideInAPackage("..controller..")
                .should().beAnnotatedWith(RestController.class)
                .check(classes);
    }

    @Test
    void servicesShouldHaveServiceAnnotation() {
        classes()
                .that().resideInAPackage("..service..")
                .should().beAnnotatedWith(Service.class)
                .check(classes);
    }

    @Test
    void repositoriesShouldBeInterfaces() {
        classes()
                .that().resideInAPackage("..repository..")
                .should().beInterfaces()
                .check(classes);
    }

    @Test
    void controllersShouldNotDependOnOtherControllers() {
        noClasses()
                .that().resideInAPackage("..controller..")
                .should().dependOnClassesThat().resideInAPackage("..controller..")
                .check(classes);
    }

    @Test
    void servicesShouldNotDependOnControllers() {
        noClasses()
                .that().resideInAPackage("..service..")
                .should().dependOnClassesThat().resideInAPackage("..controller..")
                .check(classes);
    }

    @Test
    void repositoriesShouldNotDependOnServices() {
        noClasses()
                .that().resideInAPackage("..repository..")
                .should().dependOnClassesThat().resideInAPackage("..service..")
                .check(classes);
    }

    @Test
    void controllersShouldHaveNameEndingWithController() {
        classes()
                .that().resideInAPackage("..controller..")
                .should().haveSimpleNameEndingWith("Controller")
                .check(classes);
    }

    @Test
    void servicesShouldHaveNameEndingWithService() {
        classes()
                .that().resideInAPackage("..service..")
                .should().haveSimpleNameEndingWith("Service")
                .check(classes);
    }

    @Test
    void repositoriesShouldHaveNameEndingWithRepository() {
        classes()
                .that().resideInAPackage("..repository..")
                .should().haveSimpleNameEndingWith("Repository")
                .check(classes);
    }

    @Test
    void controllersShouldNotHaveAutowiredFields() {
        noFields()
                .that().areDeclaredInClassesThat().resideInAPackage("..controller..")
                .should().beAnnotatedWith(Autowired.class)
                .check(classes);
    }

    @Test
    void mappersShouldBeInterfaces() {
        classes()
                .that().resideInAPackage("..mapper..")
                .and().haveSimpleNameNotEndingWith("Impl")
                .should().beInterfaces()
                .check(classes);
    }

    @Test
    void modelClassesShouldNotBePublicFields() {
        fields()
                .that().areDeclaredInClassesThat().resideInAPackage("..model..")
                .should().notBePublic()
                .check(classes);
    }

    @Test
    void dtoClassesShouldResideInDtoPackage() {
        classes()
                .that().haveSimpleNameEndingWith("DTO")
                .should().resideInAPackage("..dto..")
                .check(classes);
    }

    @Test
    void mappersShouldResideInMapperPackage() {
        classes()
                .that().haveSimpleNameEndingWith("Mapper")
                .should().resideInAPackage("..mapper..")
                .check(classes);
    }

    @Test
    void modelClassesShouldBeInModelPackage() {
        classes()
                .that().areAnnotatedWith(Entity.class)
                .should().resideInAPackage("..model..")
                .check(classes);
    }

    @Test
    void controllersShouldNotAccessRepositories() {
        noClasses()
                .that().resideInAPackage("..controller..")
                .should().dependOnClassesThat().resideInAPackage("..repository..")
                .check(classes);
    }

    @Test
    void servicesShouldOnlyAccessRepositoriesModelsAndMappers() {
        noClasses()
                .that().resideInAPackage("..service..")
                .should().dependOnClassesThat().resideInAPackage("..controller..")
                .check(classes);

    }
    @Test
    void applicationShouldNotContainCycles() {
        slices()
                .matching("com.example.WebDeliverySQL.(*)..")
                .should().beFreeOfCycles()
                .check(classes);
    }
    @Test
    void restControllersShouldOnlyHavePublicMethods() {
        methods()
                .that().areDeclaredInClassesThat().areAnnotatedWith(RestController.class)
                .should().bePublic()
                .check(classes);
    }
}
