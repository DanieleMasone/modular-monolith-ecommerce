package com.dmasone.identity.ecommerceapp.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "com.dmasone.identity", importOptions = DoNotIncludeTests.class)
class ArchitectureRulesTest {

    @ArchTest
    static final ArchRule sharedKernelIsIndependent = noClasses()
            .that().resideInAPackage("..sharedkernel..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("..catalog..", "..orders..", "..payment..", "..ecommerceapp..");

    @ArchTest
    static final ArchRule domainDoesNotDependOnRestOrInfrastructure = noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("..interfaces.rest..", "..infrastructure..");

    @ArchTest
    static final ArchRule domainDoesNotDependOnSpring = noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("org.springframework..");

    @ArchTest
    static final ArchRule ordersDoesNotDependOnPayment = noClasses()
            .that().resideInAPackage("..orders..")
            .should().dependOnClassesThat()
            .resideInAPackage("..payment..");

    @ArchTest
    static final ArchRule modulesDoNotReachIntoCatalogPersistence = noClasses()
            .that().resideInAnyPackage("..orders..", "..payment..")
            .should().dependOnClassesThat()
            .resideInAPackage("..catalog.infrastructure..");

    @ArchTest
    static final ArchRule paymentReactsToOrderPlacedEvent = classes()
            .that().haveSimpleName("OrderPlacedPaymentListener")
            .should().dependOnClassesThat()
            .haveFullyQualifiedName("com.dmasone.identity.orders.application.events.OrderPlacedEvent");
}
