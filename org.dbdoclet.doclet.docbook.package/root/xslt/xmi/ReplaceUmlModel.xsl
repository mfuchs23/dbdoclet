<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:UML="org.omg/standards/UML"
  version="1.0">

  <xsl:output 
    method="xml"
    version="1.0"
    indent="yes"
    encoding="UTF-8"/>

  <xsl:param name="filename" select="'UML.xmi'"/>

  <xsl:template match="/ | @* | node()">
    <xsl:copy>
      <xsl:apply-templates select="@* | node()" />
    </xsl:copy>
  </xsl:template>

  <xsl:template match="XMI.content">
    <XMI.content>
      <xsl:apply-templates select="document($filename)"/>
    </XMI.content>
  </xsl:template>

</xsl:stylesheet>
