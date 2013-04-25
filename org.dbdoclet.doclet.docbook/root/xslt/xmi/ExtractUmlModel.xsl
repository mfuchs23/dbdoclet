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

  <xsl:template match="text()">
  </xsl:template>

  <xsl:template match="UML:Model//text()">
    <xsl:value-of select="."/>
  </xsl:template>

  <xsl:template match="UML:Model">
    <xsl:copy-of select="."/>
  </xsl:template>

</xsl:stylesheet>
