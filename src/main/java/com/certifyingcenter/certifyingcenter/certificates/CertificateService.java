package com.certifyingcenter.certifyingcenter.certificates;

import com.certifyingcenter.certifyingcenter.entryies.CertificateEntity;
import com.certifyingcenter.certifyingcenter.models.CertificateInformation;
import com.certifyingcenter.certifyingcenter.repositories.CertificateEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import java.security.cert.X509Certificate;
import java.util.Date;


@Service
public class CertificateService {


    private final CertificateEntityRepository certificateEntityRepository;
    private final DateUtil dateUtil;
    private final CertificateUtil certificateUtil;

    @Autowired
    public CertificateService(CertificateEntityRepository certificateEntityRepository, DateUtil dateUtil, CertificateUtil certificateUtil) {
        this.certificateEntityRepository = certificateEntityRepository;
        this.dateUtil = dateUtil;
        this.certificateUtil = certificateUtil;
    }


    public CertificateInformation getCertInfoById(long id) {

        CertificateEntity certificate = certificateEntityRepository.findByCertificateId(id);

        CertificateInformation cInfo = new CertificateInformation();
        cInfo.setNameAndLastName(certificate.getFirstName() + " " + certificate.getLastName());
        cInfo.setTimeOfApplication(dateUtil.convertDateToString(certificate.getTimeOfApplication()));
        byte[] arr = certificate.getCertificateByteArr();

        X509Certificate x509Certificate = certificateUtil.convertByteArrToCertificate(arr);
        Date notBefore = x509Certificate.getNotBefore();
        Date notAfter = x509Certificate.getNotAfter();
        String timeNotBeforeString = dateUtil.convertDateToString(notBefore);
        String timeNotAfterString = dateUtil.convertDateToString(notAfter);

        cInfo.setTimeOfNotBefore(timeNotBeforeString);
        cInfo.setTimeOfNotAfter(timeNotAfterString);
        cInfo.setEmail(certificate.getEmailCert());
        try {
            String dn = x509Certificate.getSubjectX500Principal().getName();
            LdapName ldapDN = new LdapName(dn);
            for (Rdn rdn : ldapDN.getRdns()) {
                if (rdn.getType().equals("CN")) {
                    cInfo.setDomainName(rdn.getValue().toString());
                } else if (rdn.getType().equals("OU")) {
                    cInfo.setDepartment(rdn.getValue().toString());
                } else if (rdn.getType().equals("O")) {
                    cInfo.setOrganization(rdn.getValue().toString());
                } else if (rdn.getType().equals("L")) {
                    cInfo.setCity(rdn.getValue().toString());
                } else if (rdn.getType().equals("ST")) {
                    cInfo.setRegion(rdn.getValue().toString());
                } else if (rdn.getType().equals("C")) {
                    cInfo.setCountry(rdn.getValue().toString());
                }
            }


        } catch (Exception ignore) {
        }

        cInfo.setCert(certificateUtil.convertCertificateToString(x509Certificate));
        return cInfo;
    }
}
