package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.api.SpendApiClient;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.UUID;

public class CategoryExtension implements BeforeEachCallback, AfterTestExecutionCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CategoryExtension.class);
    private final SpendApiClient spendApiClient = new SpendApiClient();

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .ifPresent(anno -> {
                    if (anno.categories().length > 0) {
                        Category annoCategory = anno.categories()[0];

                        CategoryJson created = spendApiClient.createCategory(new CategoryJson(
                                UUID.randomUUID(),
                                annoCategory.name().isEmpty()
                                        ? RandomDataUtils.randomCategoryName()
                                        : annoCategory.name(),
                                anno.username(),
                                false
                        ));

                        if (annoCategory.archived()) {
                            created = spendApiClient.editCategory(new CategoryJson(
                                    created.id(),
                                    created.name(),
                                    created.username(),
                                    true
                            ));
                        }

                        context.getStore(NAMESPACE).put(
                                context.getUniqueId(),
                                created
                        );
                    }
                });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(CategoryJson.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), CategoryJson.class);
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        CategoryJson category = context.getStore(NAMESPACE).get(context.getUniqueId(), CategoryJson.class);
        if (category != null && !category.archived()) {
            spendApiClient.editCategory(new CategoryJson(
                    category.id(),
                    category.name(),
                    category.username(),
                    true
            ));
        }
    }

}
