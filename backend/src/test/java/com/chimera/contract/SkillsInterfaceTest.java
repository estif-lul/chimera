package com.chimera.contract;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Contract tests defining the backend skill interface boundary expected by the skill specs.
 */
class SkillsInterfaceTest {

    private static final Path MAIN_JAVA = Paths.get("src", "main", "java");

    @Test
    @DisplayName("Signal ingestion skill accepts the documented request envelope")
    void ingestPlatformSignalsSkill_acceptsDocumentedRequestEnvelope() throws Exception {
        Class<?> skillType = loadType("IngestPlatformSignalsSkill.java");
        assertTrue(skillType.isInterface(), "IngestPlatformSignalsSkill must be declared as an interface");

        Method entryPoint = singleAbstractMethod(skillType);
        assertEquals(1, entryPoint.getParameterCount(),
                "Skill interfaces should accept one request object instead of loose scalar arguments");

        Class<?> requestType = entryPoint.getParameterTypes()[0];
        assertHasAccessors(requestType, List.of(
                "tenantWorkspaceId",
                "chimeraAgentId",
                "correlationId",
                "platform",
                "resourceType",
                "resourceUri",
                "occurredAt",
                "payload"
        ));
    }

    @Test
    @DisplayName("Media generation skill exposes budget-aware failure handling")
    void generateMediaArtifactSkill_exposesBudgetAwareFailureHandling() throws Exception {
        Class<?> skillType = loadType("GenerateMediaArtifactSkill.java");
        assertTrue(skillType.isInterface(), "GenerateMediaArtifactSkill must be declared as an interface");

        Method entryPoint = singleAbstractMethod(skillType);
        assertEquals(1, entryPoint.getParameterCount(),
                "Skill interfaces should accept one request object instead of loose scalar arguments");

        Class<?> requestType = entryPoint.getParameterTypes()[0];
        assertHasAccessors(requestType, List.of(
                "tenantWorkspaceId",
                "chimeraAgentId",
                "campaignId",
                "taskId",
                "correlationId",
                "assetType",
                "providerTool",
                "promptPackage"
        ));

        Class<?> budgetExceededException = loadType("BudgetExceededException.java");
        assertTrue(Exception.class.isAssignableFrom(budgetExceededException),
                "BudgetExceededException must be an exception type");
        assertTrue(Arrays.asList(entryPoint.getExceptionTypes()).contains(budgetExceededException),
                "Media skill entry point should declare BudgetExceededException for budget guardrail failures");
    }

    private static void assertHasAccessors(Class<?> type, List<String> accessorNames) {
        for (String accessorName : accessorNames) {
            try {
                type.getMethod(accessorName);
            } catch (NoSuchMethodException ex) {
                throw new AssertionFailedError(
                        "Expected accessor '" + accessorName + "' on request type " + type.getName(), ex);
            }
        }
    }

    private static Method singleAbstractMethod(Class<?> interfaceType) {
        List<Method> abstractMethods = Arrays.stream(interfaceType.getDeclaredMethods())
                .filter(method -> Modifier.isAbstract(method.getModifiers()))
                .toList();

        assertEquals(1, abstractMethods.size(),
                () -> interfaceType.getName() + " should expose a single abstract entry point");
        return abstractMethods.getFirst();
    }

    private static Class<?> loadType(String simpleFileName) throws Exception {
        Path sourceFile = findSourceFile(simpleFileName);
        String packageName = readPackageName(sourceFile);
        String simpleClassName = simpleFileName.substring(0, simpleFileName.length() - ".java".length());
        return Class.forName(packageName + "." + simpleClassName);
    }

    private static Path findSourceFile(String simpleFileName) throws IOException {
        try (var paths = Files.walk(MAIN_JAVA)) {
            return paths
                    .filter(path -> path.getFileName().toString().equals(simpleFileName))
                    .findFirst()
                    .orElseThrow(() -> new AssertionFailedError(
                            "Expected backend source file '" + simpleFileName
                                    + "' to exist for the documented skills contract"));
        }
    }

    private static String readPackageName(Path sourceFile) throws IOException {
        return Files.lines(sourceFile)
                .map(String::trim)
                .filter(line -> line.startsWith("package "))
                .map(line -> line.substring("package ".length(), line.length() - 1))
                .findFirst()
                .orElseThrow(() -> new AssertionFailedError(
                        "Expected a package declaration in source file " + sourceFile));
    }
}