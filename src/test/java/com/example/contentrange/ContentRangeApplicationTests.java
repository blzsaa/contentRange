package com.example.contentrange;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@WebAppConfiguration
class ContentRangeApplicationTests {

  public static final String EXPECTED_FULL_CONTENT =
      "[{\"lc\":\"a\"},{\"lc\":\"b\"},{\"lc\":\"c\"}]";

  private MockMvc mockMvc;
  @Autowired private WebApplicationContext webApplicationContext;

  @BeforeEach
  public void setUp() {
    mockMvc =
        MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .addFilter(new AddResponseHeaderFilter())
            .build();
  }

  @Test
  void shouldNotAddContentRangeWhenRequestHasNoRangeHeader() throws Exception {
    mockMvc
        .perform(get("/lc/all"))
        .andExpect(content().string(EXPECTED_FULL_CONTENT))
        .andExpect(status().isOk())
        .andExpect(header().doesNotExist("Content-Range"));
  }

  @Test
  void shouldGiveBackFirst11BytesOfTheMsgWhenRange0_10IsSentInRequest() throws Exception {
    mockMvc
        .perform(get("/lc/all").header("Range", "bytes=0-10"))
        .andExpect(content().string(EXPECTED_FULL_CONTENT.substring(0, 11)))
        .andExpect(status().isOk())
        .andExpect(header().string("Content-Range", "bytes 0-10/34"))
        .andExpect(header().string("Content-Length", "11"));
  }

  @Test
  void rangeShouldWorkForMiddlePartAsWell() throws Exception {
    mockMvc
        .perform(get("/lc/all").header("Range", "bytes=10-22"))
        .andExpect(content().string(EXPECTED_FULL_CONTENT.substring(10, 23)))
        .andExpect(status().isOk())
        .andExpect(header().string("Content-Range", "bytes 10-22/34"))
        .andExpect(header().string("Content-Length", "13"));
  }

  @Test
  void rangeShouldWorkWhenOnlyLowerPartIsGiven() throws Exception {
    mockMvc
        .perform(get("/lc/all").header("Range", "bytes=10-"))
        .andExpect(content().string(EXPECTED_FULL_CONTENT.substring(10)))
        .andExpect(status().isOk())
        .andExpect(header().string("Content-Range", "bytes 10-33/34"))
        .andExpect(header().string("Content-Length", "24"));
  }
}
