package pt.ul.fc.css.soccernow.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PlayerWebControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void listPlayersPage() throws Exception {
        mockMvc.perform(get("/web/players"))
                .andExpect(status().isOk())
                .andExpect(view().name("players/list"))
                .andExpect(model().attributeExists("players"))
                .andExpect(model().attributeExists("positions"));
    }
}
