package com.example.prac.elements;

import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class GradeAdvanceService {

    private States states;
    private TargetsGen targetGenerator;
    private TargetsExporter targetsExporter;
    private AdvanceApplier applier;
    private TargetsImporter importer;
    public static final Path DEFAULT_TARGETS_FILE = Paths.get("build/targets");
    private static final String EMPTY_VALUE = "EMPTY_VALUE";
    private Path targetsFilePath = DEFAULT_TARGETS_FILE;

    public GradeAdvanceService(States states, TargetsGen targetGenerator
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
            // 만약 실패했던애 다시 승급 시도하려면 대상자 재선정 및 저장 x
            // import해서 이전 대상자들 불러오고 승급 시도
            Either<AdvanceResult, Targets> importResult = importTargets();
            if (importResult.isLeft()) {
                return importResult.getLeftValue();
            }
            targets = importResult.getRightValue();

        } else {
            // 그게 아니라면 대상자 추출 및 저장 진행 후 승급 시도
            Either<AdvanceResult, Targets> genResult = genTargets();
            if (genResult.isLeft()) {
                return genResult.getLeftValue();
            }
            targets = genResult.getRightValue();

            Either<AdvanceResult, Object> exportResult = exportTargets(targets);
            if (exportResult.isLeft()) {
                return exportResult.getLeftValue();
            }
        }

        Either<AdvanceResult, Object> applyResult = applyTargets(targets);
        if (applyResult.isLeft()) {
            return applyResult.getLeftValue();
        }

        return AdvanceResult.SUCCESS;
    }

    private Either<AdvanceResult, Object> applyTargets(Targets targets) {
        try {
            applier.apply(targets);
            return Either.right(EMPTY_VALUE);
        } catch (Exception e) {
            states.set(AdvanceState.APPLY_FAILED);
            return Either.left(AdvanceResult.TARGET_APPLY_FAIL);
        }
    }

    private Either<AdvanceResult, Targets> importTargets() {
        try {
            Targets targets = importer.importTargets(targetsFilePath);
            return Either.right(targets);
        } catch (Exception e) {
            return Either.left(AdvanceResult.TARGET_IMPORT_FAIl);
        }
    }

    private Either<AdvanceResult, Targets> genTargets() {
        try {
            Targets targets = targetGenerator.gen();
            return Either.right(targets);
        } catch (Exception e) {
            return Either.left(AdvanceResult.TARGET_GEN_FAIL);
        }
    }

    private Either<AdvanceResult, Object> exportTargets(Targets targets) {
        try {
            targetsExporter.export(targetsFilePath, targets);
            return Either.right(EMPTY_VALUE);
        } catch (Exception e) {
            return Either.left(AdvanceResult.TARGET_EXPORT_FAIL);
        }
    }
}
