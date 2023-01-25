package com.example.prac;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class GradeAdvanceServiceTest {

    private final States states = new States(Paths.get("build/state"));
    private final TargetGen mockGen = mock(TargetGen.class);
    private final TargetsExporter mockExporter = mock(TargetsExporter.class);
    private final AdvanceApplier mockApplier = mock(AdvanceApplier.class);
    private final TargetsImporter mockImporter = mock(TargetsImporter.class);
    private final GradeAdvanceService service = new GradeAdvanceService(states,mockGen,
            mockExporter,mockApplier,mockImporter);

    @BeforeEach
    void setUp() throws IOException {
        Files.deleteIfExists(Path.of("build/state"));
    }

    @Test
    @DisplayName("이미 승급완료인 경우 exception")
    void exceptionWithAlreadyCompletedStates() {
        states.set(AdvanceState.COMPLETED);
        AdvanceResult result = service.advance();
        assertThat(result).isEqualTo(AdvanceResult.ALREADY_COMPLETED);
    }

    @Test
    @DisplayName("대상자 추출시 에러가 발생하면 실패 result 반환")
    void targetsGenFail() {
        // 타겟 추출 도중에 예외가 발생해버릴것이다.
        BDDMockito.given(mockGen.gen()).willThrow(new RuntimeException("@!"));

        AdvanceResult result = service.advance();
        // 그러면 결과가 gen_fail로 넘어올것이다
        assertThat(result).isEqualTo(AdvanceResult.TARGET_GEN_FAIL);
    }

    @Test
    @DisplayName("대상자 추출 후 그 목록 저장시 예외가 발생한다")
    void targetExportsFail() {
        // 대상자는 추출해서 targets를 반환해준다
        BDDMockito.given(mockGen.gen()).willReturn(mock(Targets.class));

        // 그런데 이 대상자를 저장해놓는 과정에서 예외가 터질것이다.
        BDDMockito.willThrow(new RuntimeException("!"))
                .given(mockExporter).export(Mockito.any(Path.class), Mockito.any(Targets.class));

        AdvanceResult result = service.advance();
        assertThat(result).isEqualTo(AdvanceResult.TARGET_EXPORT_FAIL);
    }


    @Test
    @DisplayName("승급 apply 실패")
    void applyFail() {
        // 대상자 추출
        BDDMockito.given(mockGen.gen()).willReturn(mock(Targets.class));
        // 추출된 대상자 저장시 예외없으니 넘어가고
        // 승급 과정에서 실패
        BDDMockito.given(mockApplier.apply(Mockito.any(Targets.class)))
                .willThrow(new RuntimeException("!@"));

        AdvanceResult result = service.advance();
        assertThat(result).isEqualTo(AdvanceResult.TARGET_APPLY_FAIL);
    }

    @Test
    @DisplayName("승급 apply 실패의 경우 state가 applyFailed로 변경되어야한다")
    void applyFail_Then_State_Be_ApplyFailed() {
        // 대상자 추출
        BDDMockito.given(mockGen.gen()).willReturn(mock(Targets.class));
        // 추출된 대상자 저장시 예외없으니 넘어가고
        // 승급 과정에서 실패
        BDDMockito.given(mockApplier.apply(Mockito.any(Targets.class)))
                .willThrow(new RuntimeException("!@"));

        service.advance();
        assertThat(states.get()).isEqualTo(AdvanceState.APPLY_FAILED);
    }

    @Test
    @DisplayName("state가 applyFailed인 대상을 advance하는 경우")
    void advance_with_Already_ApplyFailed() {
        states.set(AdvanceState.APPLY_FAILED);
        Targets targets = new Targets(null);
        BDDMockito.given(mockImporter.importTargets(Mockito.any(Path.class)))
                .willReturn(targets);
        service.advance();

        // apply_failed 대상으론 대상자 추출, 저장은 동작하면 안된다
        BDDMockito.then(mockGen).shouldHaveNoInteractions();
        BDDMockito.then(mockExporter).shouldHaveNoInteractions();
        BDDMockito.then(mockApplier).should().apply(targets);
    }
    @Test
    @DisplayName("승급 apply 성공")
    void applySuccess() {
        // 대상자 추출
        BDDMockito.given(mockGen.gen()).willReturn(mock(Targets.class));
        // 추출된 대상자 저장시 예외없으니 넘어가고
        // 승급 과정에서 실패없으니 결과를 리턴하고
        BDDMockito.given(mockApplier.apply(Mockito.any(Targets.class)))
                .willReturn(Mockito.mock(ApplyResult.class));

        AdvanceResult result = service.advance();
        assertThat(result).isEqualTo(AdvanceResult.SUCCESS);
    }

    public static class GradeAdvanceService {

        private States states;
        private TargetGen targetGenerator;
        private TargetsExporter targetsExporter;
        private AdvanceApplier applier;
        private TargetsImporter importer;
        private static final Path DEFAULT_TARGETS_FILE = Paths.get("build/targets");
        private Path targetsFilePath = DEFAULT_TARGETS_FILE;
        public GradeAdvanceService(States states, TargetGen targetGenerator
                , TargetsExporter targetsExporter, AdvanceApplier applier, TargetsImporter importer) {
            this.states = states;
            this.targetGenerator = targetGenerator;
            this.targetsExporter = targetsExporter;
            this.applier = applier;
            this.importer = importer;
        }

        public AdvanceResult advance() {
            AdvanceState state = states.get();
            if (state == AdvanceState.COMPLETED) {
                return AdvanceResult.ALREADY_COMPLETED;
            }

            Targets targets;
            if (state == AdvanceState.APPLY_FAILED) {
                targets = importer.importTargets(targetsFilePath);
            } else {
                try {
                    targets = targetGenerator.gen();
                } catch (Exception e) {
                    return AdvanceResult.TARGET_GEN_FAIL;
                }


                try {
                    targetsExporter.export(targetsFilePath, targets);
                } catch (Exception e) {
                    return AdvanceResult.TARGET_EXPORT_FAIL;
                }
            }


            try {
                applier.apply(targets);
            } catch (Exception e) {
                states.set(AdvanceState.APPLY_FAILED);
                return AdvanceResult.TARGET_APPLY_FAIL;
            }
            return AdvanceResult.SUCCESS;
        }
    }


    private enum AdvanceResult {
        TARGET_GEN_FAIL, TARGET_EXPORT_FAIL, TARGET_APPLY_FAIL, SUCCESS, ALREADY_COMPLETED

    }

}
