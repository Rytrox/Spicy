package de.rytrox.spicy.skin.mineskin;

import com.google.gson.JsonParser;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class MineskinBuilderTest {

    @Test
    public void shouldCallCache() throws ExecutionException, InterruptedException, TimeoutException {
        try(MockedStatic<MineskinHandler> mockedStatic = Mockito.mockStatic(MineskinHandler.class)) {
            mockedStatic.when(() -> MineskinHandler.getMineskin(Mockito.any()))
                    .thenAnswer((invocationOnMock) ->
                            CompletableFuture.supplyAsync(() ->
                                    {
                                        try {
                                            return new Mineskin(JsonParser.parseString(Files.readString(Paths.get("src", "test", "resources", "mineskin", "skin.json"))).getAsJsonObject());
                                        } catch (IOException e) {
                                            throw new CompletionException(e);
                                        }
                                    }

                            )
                    );

            Mineskin mineskin = MineskinBuilder.loadMineskin(UUID.randomUUID())
                    .get(5, TimeUnit.SECONDS);

            assertNotNull(mineskin);
        }
    }

    @Test
    public void shouldUpdateGeneratingOptions() throws IllegalAccessException {
        MineskinBuilder builder = new MineskinBuilder()
                .variant(GenerateOptions.Variant.SLIM)
                .visibility(GenerateOptions.Visibility.PUBLIC)
                .name("Test");

        GenerateOptions options = (GenerateOptions) FieldUtils.readField(builder, "options", true);

        assertEquals(GenerateOptions.Variant.SLIM.getType(), options.getVariant());
        assertEquals(GenerateOptions.Visibility.PUBLIC.getType(), options.getVisibility());
        assertEquals("Test", options.getName());


    }
}
