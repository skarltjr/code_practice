package com.example.prac;

import java.nio.file.Path;
import java.nio.file.Paths;

public class GradeAdvanceService {

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
