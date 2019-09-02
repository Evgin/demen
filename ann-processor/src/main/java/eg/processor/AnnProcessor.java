package eg.processor;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ExecutableType;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({
        "eg.ann.Handler"
})
public class AnnProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        log.info("annotations : {}", annotations);
        log.info("roundEnv : {}", roundEnv);

        for (TypeElement annotation : annotations) {
            Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
            log.info("annotatedElements : {}", annotatedElements);
            annotatedElements
                    .forEach(el -> {
                        ExecutableType exeType = (ExecutableType) el.asType();
                        log.info("{} {}({}) throws {}",
                                exeType.getReturnType(),
                                el.getSimpleName(),
                                exeType.getParameterTypes(),
                                exeType.getThrownTypes());
                    });

            annotatedElements.stream().map(el -> (ExecutableElement) el).forEach(el1 -> {
                try {
                    createHandlerInterface(el1);
                } catch (IOException e) {
                    log.error("unable create java source file", e);
                }
            });

        }

        return false;
    }

    private void createHandlerInterface(ExecutableElement el) throws IOException {

        Element enclosingElement = el.getEnclosingElement();
        String className = ((TypeElement) enclosingElement).getQualifiedName().toString();
        log.info("enclosingTypeName : {}", className);

        String packageName = null;
        int lastDot = className.lastIndexOf('.');
        if (lastDot > 0) {
            packageName = className.substring(0, lastDot);
        }

        String simpleClassName = className.substring(lastDot + 1);
        String interfaceName = className + "Handler";
        String interfaceSimpleClassName = interfaceName.substring(lastDot + 1);

        String methodName = String.valueOf(el.getSimpleName());

        JavaFileObject interfaceHandlerFile = processingEnv.getFiler()
                .createSourceFile(className + "Handler");

        try (PrintWriter out = new PrintWriter(interfaceHandlerFile.openWriter())) {
            if (packageName != null) {
                out.print("package ");
                out.print(packageName);
                out.println(";");
                out.println();
            }

            out.print("public interface ");
            out.print(interfaceSimpleClassName);
            out.println(" {");
            out.println();

            out.print("void " + methodName + "Handle");
            out.print("(");
            ///////
            String params = el.getParameters().stream()
                    .map(param -> ((VariableElement) param))
                    .map(ve -> ve.asType().toString() + " " + ve.getSimpleName())
                    .collect(Collectors.joining(", "));


            String throwns = IntStream.range(0, el.getThrownTypes().size())
                    .mapToObj(i -> (el.getThrownTypes().get(i) + " " + "ex" + i))
                    .collect(Collectors.joining(", "));

            String newMethodParams = "";
            if (params != null && !params.isEmpty()) {
                newMethodParams += params;
            }
            if (throwns != null && !throwns.isEmpty()) {
                newMethodParams += (!newMethodParams.isEmpty() ? ", " : "") + throwns;
            }

            out.print(newMethodParams);
            //////
            out.print(")");
            out.println(";");
            out.println();

            out.println("}");
        }
    }

}
