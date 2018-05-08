package org.icgc.dcc.song.server.model.enums;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class ModelAttributeNames {

  public static final String DONOR_ID = "donorId";
  public static final String DONOR_SUBMITTER_ID = "donorSubmitterId";
  public static final String STUDY_ID = "studyId";
  public static final String SPECIMEN_ID = "specimenId";
  public static final String SAMPLE_ID = "sampleId";
  public static final String OBJECT_ID = "objectId";
  public static final String DONOR_GENDER = "donorGender";
  public static final String SPECIMENS = "specimens";
  public static final String INFO = "info";
  public static final String ANALYSIS_ID = "analysisId";
  public static final String ALIGNED = "aligned";
  public static final String ALIGNMENT_TOOL = "alignmentTool";
  public static final String INSERT_SIZE = "insertSize";
  public static final String LIBRARY_STRATEGY = "libraryStrategy";
  public static final String PAIRED_END = "pairedEnd";
  public static final String REFERENCE_GENOME = "referenceGenome";
  public static final String UPLOAD_ID = "uploadId";
  public static final String STATE = "state";
  public static final String CREATED_AT = "createdAt";
  public static final String UPDATED_AT = "updatedAt";
  public static final String ERRORS = "errors";
  public static final String PAYLOAD = "payload";
}
