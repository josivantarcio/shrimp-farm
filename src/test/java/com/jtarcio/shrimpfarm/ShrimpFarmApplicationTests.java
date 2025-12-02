package com.jtarcio.shrimpfarm;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ShrimpFarmApplicationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    @DisplayName("Deve carregar o contexto da aplicação")
    void deveCarregarContextoDaAplicacao() {
        assertThat(applicationContext).isNotNull();
    }

    @Test
    @DisplayName("Deve ter a classe ShrimpFarmApplication no contexto")
    void deveTerClassePrincipalNoContexto() {
        assertThat(applicationContext.getBean(ShrimpFarmApplication.class)).isNotNull();
    }

    @Test
    @DisplayName("Deve ter a anotação @SpringBootApplication")
    void deveTerAnotacaoSpringBootApplication() {
        assertThat(ShrimpFarmApplication.class.isAnnotationPresent(
                org.springframework.boot.autoconfigure.SpringBootApplication.class
        )).isTrue();
    }

    @Test
    @DisplayName("Deve ter o método main")
    void deveTerMetodoMain() throws NoSuchMethodException {
        var mainMethod = ShrimpFarmApplication.class.getDeclaredMethod("main", String[].class);

        assertThat(mainMethod).isNotNull();
        assertThat(mainMethod.getReturnType()).isEqualTo(void.class);
        assertThat(java.lang.reflect.Modifier.isStatic(mainMethod.getModifiers())).isTrue();
        assertThat(java.lang.reflect.Modifier.isPublic(mainMethod.getModifiers())).isTrue();
    }

    @Test
    @DisplayName("Deve ter construtor público")
    void deveTerConstrutorPublico() {
        var constructors = ShrimpFarmApplication.class.getConstructors();

        assertThat(constructors).isNotEmpty();
        assertThat(constructors[0].getModifiers() & java.lang.reflect.Modifier.PUBLIC).isNotZero();
    }
}
