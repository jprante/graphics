package org.xbib.graphics.barcode;

import static java.util.Collections.singletonList;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;
import org.junit.platform.commons.util.CollectionUtils;
import org.junit.platform.commons.util.ReflectionUtils;
import org.junit.runners.Parameterized;

public class ParameterizedExtension implements TestTemplateInvocationContextProvider {

	private final static ExtensionContext.Namespace PARAMETERS = ExtensionContext.Namespace.create(
		ParameterizedExtension.class);

	/**
	 * Indicate whether we can provide parameterized support.
	 * This requires the testClass to either have a static {@code @Parameters} method
	 * and correct {@code @Parameter} and their corresponding values
	 * or to have a constructor that could be injected.
	 */
	public boolean supportsTestTemplate(ExtensionContext context) {
		return hasParametersMethod(context) && validInjectionMix(context);
	}

	private static boolean validInjectionMix(ExtensionContext context) {
		List<Field> fields = parametersFields(context);
		boolean hasParameterFields = !fields.isEmpty();
		boolean hasCorrectParameterFields = areParametersFormedCorrectly(fields);
		boolean hasArgsConstructor = hasArgsConstructor(context);

		if (hasArgsConstructor) {
			return !hasParameterFields;
		}
		else {
			return !hasParameterFields || hasCorrectParameterFields;
		}
	}

	@Override
	public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {
		// grabbing the parent ensures the PARAMETERS are stored in the same store across multiple TestTemplates.
		return context.getParent().flatMap(ParameterizedExtension::parameters).map(
			o -> testTemplateContextsFromParameters(o, context)).orElse(Stream.empty());
	}

	private static boolean areParametersFormedCorrectly(List<Field> fields) {
		List<Integer> parameterValues = parameterIndexes(fields);
		List<Integer> duplicateIndexes = duplicatedIndexes(parameterValues);
		boolean hasAllIndexes = indexRangeComplete(parameterValues);

		return hasAllIndexes && duplicateIndexes.isEmpty();
	}

	private static List<Integer> parameterIndexes(List<Field> fields) {
		// @formatter:off
		return fields.stream()
				.map(f -> f.getAnnotation(Parameterized.Parameter.class))
				.map(Parameterized.Parameter::value)
				.collect(toList());
		// @formatter:on
	}

	private static List<Integer> duplicatedIndexes(List<Integer> parameterValues) {
		// @formatter:off
		return parameterValues.stream().collect(groupingBy(identity())).entrySet().stream()
				.filter(e -> e.getValue().size() > 1)
				.map(Map.Entry::getKey)
				.collect(toList());
		// @formatter:on
	}

	private static Boolean indexRangeComplete(List<Integer> parameterValues) {
		// @formatter:off
		return parameterValues.stream()
				.max(Integer::compareTo)
				.map(i -> parameterValues.containsAll(IntStream.range(0, i).boxed().collect(toList())))
				.orElse(false);
		// @formatter:on
	}

	private static Optional<Collection<Object[]>> parameters(ExtensionContext context) {
		return context.getStore(PARAMETERS).getOrComputeIfAbsent("parameterMethod",
			k -> new ParameterWrapper(callParameters(context)), ParameterWrapper.class).getValue();

	}

	private static Optional<Collection<Object[]>> callParameters(ExtensionContext context) {
		// @formatter:off
		return findParametersMethod(context)
				.map(m -> ReflectionUtils.invokeMethod(m, null))
				.map(ParameterizedExtension::convertParametersMethodReturnType);
		// @formatter:on
	}

	private static boolean hasParametersMethod(ExtensionContext context) {
		return findParametersMethod(context).isPresent();
	}

	private static Optional<Method> findParametersMethod(ExtensionContext extensionContext) {
		// @formatter:off
		return extensionContext.getTestClass()
				.flatMap(ParameterizedExtension::ensureSingleParametersMethod)
				.filter(ReflectionUtils::isPublic);
		// @formatter:on
	}

	private static Optional<Method> ensureSingleParametersMethod(Class<?> testClass) {
		return ReflectionUtils.findMethods(testClass,
			m -> m.isAnnotationPresent(Parameterized.Parameters.class)).stream().findFirst();
	}

	private static Stream<TestTemplateInvocationContext> testTemplateContextsFromParameters(Collection<Object[]> o,
			ExtensionContext context) {
		List<Field> fields = parametersFields(context);
		boolean hasParameterFields = !fields.isEmpty();
		boolean hasCorrectParameterFields = areParametersFormedCorrectly(fields);

		if (!hasParameterFields) {
			return o.stream().map(ParameterizedExtension::parameterResolver);
		}
		else if (hasCorrectParameterFields) {
			return o.stream().map(ParameterizedExtension::contextFactory);
		}

		return Stream.empty();
	}

	private static TestTemplateInvocationContext parameterResolver(Object[] objects) {
		List<Extension> parameterResolvers = singletonList(new ParameterResolver() {
			@Override
			public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
					throws ParameterResolutionException {
				final Executable declaringExecutable = parameterContext.getDeclaringExecutable();
				return declaringExecutable instanceof Constructor
						&& declaringExecutable.getParameterCount() == objects.length;
			}

			@Override
			public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
					throws ParameterResolutionException {
				return objects[parameterContext.getIndex()];
			}
		});

		return templateWithExtensions(parameterResolvers);
	}

	private static TestTemplateInvocationContext contextFactory(Object[] parameters) {
		return templateWithExtensions(singletonList(new InjectionExtension(parameters)));
	}

	private static class InjectionExtension implements TestInstancePostProcessor {

		private final Object[] parameters;

		public InjectionExtension(Object[] parameters) {
			this.parameters = parameters;
		}

		@Override
		public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
			List<Field> parameters = parametersFields(context);

			if (!parameters.isEmpty() && parameters.size() != this.parameters.length) {
				throw unMatchedAmountOfParametersException();
			}

			for (Field param : parameters) {
				Parameterized.Parameter annotation = param.getAnnotation(Parameterized.Parameter.class);
				int paramIndex = annotation.value();
				param.set(testInstance, this.parameters[paramIndex]);
			}
		}

	}

	private static TestTemplateInvocationContext templateWithExtensions(List<Extension> extensions) {
		return new TestTemplateInvocationContext() {
			@Override
			public List<Extension> getAdditionalExtensions() {
				return extensions;
			}
		};
	}

	private static boolean hasArgsConstructor(ExtensionContext context) {
		// @formatter:off
		return context.getTestClass()
				.map(ReflectionUtils::getDeclaredConstructor)
				.filter(c -> c.getParameterCount() > 0)
				.isPresent();
		// @formatter:on
	}

	private static List<Field> parametersFields(ExtensionContext context) {
		// @formatter:off
		Stream<Field> fieldStream = context.getTestClass()
				.map(Class::getDeclaredFields)
				.map(Stream::of)
				.orElse(Stream.empty());
		// @formatter:on

		return fieldStream.filter(f -> f.isAnnotationPresent(Parameterized.Parameter.class)).filter(
			ReflectionUtils::isPublic).collect(toList());
	}

	private static ParameterResolutionException unMatchedAmountOfParametersException() {
		return new ParameterResolutionException(
			"The amount of parametersFields in the constructor doesn't match those in the provided parametersFields");
	}

	private static Collection<Object[]> convertParametersMethodReturnType(Object obj) {
		return CollectionUtils.toStream(obj).map(o -> {
			if (o instanceof Object[]) {
				return (Object[]) o;
			}
			return new Object[] { o };
		}).collect(toList());
	}

	private static class ParameterWrapper {
		private final Optional<Collection<Object[]>> value;

		public ParameterWrapper(Optional<Collection<Object[]>> value) {
			this.value = value;
		}

		public Optional<Collection<Object[]>> getValue() {
			return value;
		}
	}
}
