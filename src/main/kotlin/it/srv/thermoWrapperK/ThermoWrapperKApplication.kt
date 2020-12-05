package it.srv.thermoWrapperK

import it.srv.thermoWrapperK.dao.InfoDAO
import it.srv.thermoWrapperK.model.Info
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class ThermoWrapperKApplication

fun main(args: Array<String>) {
	runApplication<ThermoWrapperKApplication>(*args)
}