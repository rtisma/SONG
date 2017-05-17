package org.icgc.dcc.sodalite.server.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.val;

public class TestDataFactory {

  public static Donor createDonor() {
    val d = new Donor()
        .withStudyId("ABC123")
        .withDonorGender(DonorGender.MALE)
        .withDonorSubmitterId("Triangle-Arrow-S")
        .withSpecimens(new ArrayList<Specimen>());
    return d;
  }
  
  public static Specimen createSpecimen(String parentId) {
    val s = new Specimen()
        .withStudyId("ABC123")
        .withDonorId(parentId)
        .withSpecimenSubmitterId("Specimen 102 Chiron-Beta Prime")
        .withSpecimenType(SpecimenType.METASTATIC_TUMOUR_ADDITIONAL_METASTATIC)
        .withSpecimenClass(SpecimenClass.TUMOUR)
        .withSamples(new ArrayList<Sample>());
    return s;
  }
  
  public static Sample createSample(String parentId) {
    val s = new Sample()
        .withStudyId("ABC123")
        .withSpecimenId(parentId)
        .withSampleSubmitterId("101-IP-A")
        .withSampleType(SampleType.AMPLIFIED_DNA)
        .withFiles(new ArrayList<File>());
    return s;
  }
  
  public static List<File> createFileSet(String parentId) {
    File bamf = new File()
        .withStudyId("ABC123")
        .withSampleId(parentId)
        .withFileName("ABC-TC285G87-A5-sqrl.bam")
        .withFileSize(50000000000L)
        .withFileType(FileType.BAM);
    
    File baif = new File()
        .withStudyId("ABC123")
        .withSampleId(parentId)
        .withFileName("ABC-TC285G87-A5-sqrl.bai")
        .withFileSize(50000)
        .withFileType(FileType.BAI);
    
    File xmlf = new File()
        .withStudyId("ABC123")
        .withSampleId(parentId)
        .withFileName("hamsters.xml")
        .withFileSize(5000)
        .withFileType(FileType.XML);
    
    return Arrays.asList(bamf, baif, xmlf);
  }
  
  public static SequencingRead createSequencingRead() {
    SequencingRead result = new SequencingRead();
    result.withStudyId("ABC123")
      .withAnalysisSubmitterId("1fbb6bb0-caca-4c4f-8f11-72bfa9a5faef")
      .withState(AnalysisState.RECEIVED)
      .withAligned(true)
      .withAlignmentTool("Scotch Tape")
      .withInsertSize(2000)
      .withLibraryStrategy(LibraryStrategy.OTHER)
      .withPairedEnd(true)
      .withReferenceGenome("something something");
    return result;
  }
}
