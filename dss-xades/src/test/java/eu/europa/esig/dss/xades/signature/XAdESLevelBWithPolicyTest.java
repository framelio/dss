/**
 * DSS - Digital Signature Services
 * Copyright (C) 2015 European Commission, provided under the CEF programme
 * 
 * This file is part of the "DSS - Digital Signature Services" project.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package eu.europa.esig.dss.xades.signature;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Date;

import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.esig.dss.diagnostic.DiagnosticData;
import eu.europa.esig.dss.diagnostic.SignatureWrapper;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.enumerations.SignaturePackaging;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.FileDocument;
import eu.europa.esig.dss.model.Policy;
import eu.europa.esig.dss.signature.DocumentSignatureService;
import eu.europa.esig.dss.xades.XAdESSignatureParameters;

public class XAdESLevelBWithPolicyTest extends AbstractXAdESTestSignature {

	private static final Logger logger = LoggerFactory.getLogger(XAdESLevelBWithPolicyTest.class);

	private static final String HTTP_SPURI_TEST = "http://spuri.test";
	private static final String SIGNATURE_POLICY_ID = "1.2.3.4.5.6";
	private static final String SIGNATURE_POLICY_DESCRIPTION = "Test description";

	private DocumentSignatureService<XAdESSignatureParameters> service;
	private XAdESSignatureParameters signatureParameters;
	private DSSDocument documentToSign;

	@Before
	public void init() throws Exception {
		documentToSign = new FileDocument(new File("src/test/resources/sample.xml"));

		Policy signaturePolicy = new Policy();
		signaturePolicy.setId(SIGNATURE_POLICY_ID);
		signaturePolicy.setDescription(SIGNATURE_POLICY_DESCRIPTION);
		signaturePolicy.setDigestAlgorithm(DigestAlgorithm.SHA1);
		signaturePolicy.setDigestValue(new byte[] { 'd', 'i', 'g', 'e', 's', 't', 'v', 'a', 'l', 'u', 'e' });
		signaturePolicy.setSpuri(HTTP_SPURI_TEST);

		signatureParameters = new XAdESSignatureParameters();
		signatureParameters.bLevel().setSigningDate(new Date());
		signatureParameters.bLevel().setSignaturePolicy(signaturePolicy);
		signatureParameters.setSigningCertificate(getSigningCert());
		signatureParameters.setCertificateChain(getCertificateChain());
		signatureParameters.setSignaturePackaging(SignaturePackaging.ENVELOPING);
		signatureParameters.setSignatureLevel(SignatureLevel.XAdES_BASELINE_B);

		service = new XAdESService(getCompleteCertificateVerifier());

	}

	@Override
	protected void onDocumentSigned(byte[] byteArray) {
		super.onDocumentSigned(byteArray);
		String xmlContent = new String(byteArray);
		logger.info(xmlContent);
		assertTrue(xmlContent.contains("description"));
		assertTrue(xmlContent.contains(":SigPolicyQualifiers>"));
		assertTrue(xmlContent.contains(":SigPolicyQualifier>"));
		assertTrue(xmlContent.contains(HTTP_SPURI_TEST));
	}
	
	@Override
	protected void verifyDiagnosticData(DiagnosticData diagnosticData) {
		super.verifyDiagnosticData(diagnosticData);
		SignatureWrapper signature = diagnosticData.getSignatureById(diagnosticData.getFirstSignatureId());
		assertEquals(HTTP_SPURI_TEST, signature.getPolicyUrl());
		assertEquals(SIGNATURE_POLICY_ID, signature.getPolicyId());
		assertEquals(SIGNATURE_POLICY_DESCRIPTION, signature.getPolicyDescription());
	}

	@Override
	protected DocumentSignatureService<XAdESSignatureParameters> getService() {
		return service;
	}

	@Override
	protected XAdESSignatureParameters getSignatureParameters() {
		return signatureParameters;
	}

	@Override
	protected DSSDocument getDocumentToSign() {
		return documentToSign;
	}

	@Override
	protected String getSigningAlias() {
		return GOOD_USER;
	}

}
