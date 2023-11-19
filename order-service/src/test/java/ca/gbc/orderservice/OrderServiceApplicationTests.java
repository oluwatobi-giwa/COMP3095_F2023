package ca.gbc.orderservice;

import ca.gbc.orderservice.dto.OrderLineItemDto;
import ca.gbc.orderservice.dto.OrderRequest;
import ca.gbc.orderservice.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.Collections;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OrderServiceApplicationTests extends AbstractContainerBaseTest {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	private static MockWebServer mockWebServer;

	@Autowired
	private OrderRepository orderRepository;


	OrderRequest getOrderRequest() {
		Long id = 1L;
		OrderLineItemDto orderLineItemDto = new OrderLineItemDto(id, "SKU_0123", BigDecimal.valueOf(100), 7);
		return OrderRequest.builder()
				.orderLineItemDtoList(Collections.singletonList(orderLineItemDto))
				.build();
	}

	@BeforeAll
	static void setupServer() throws Exception {
		mockWebServer = new MockWebServer();
		mockWebServer.start();System.setProperty("inventory.service.url", "http://localhost:" + mockWebServer.getPort());
	}

	@AfterAll
	static void tearDownServer() throws Exception {
		mockWebServer.shutdown();
	}


	@Test
	void placeOrder() throws Exception {
		OrderRequest orderRequest = getOrderRequest();
		String orderRequestString = objectMapper.writeValueAsString(orderRequest);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/order")
						.contentType(MediaType.APPLICATION_JSON)
						.content(orderRequestString))
				.andExpect(MockMvcResultMatchers.status().isCreated());

        Assertions.assertFalse(orderRepository.findAll().isEmpty());
	}

}
