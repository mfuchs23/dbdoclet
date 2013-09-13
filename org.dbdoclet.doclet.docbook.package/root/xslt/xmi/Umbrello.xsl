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

  <xsl:template match="/">
    <XMI xmlns:UML="org.omg/standards/UML" verified="false" timestamp="" xmi.version="1.2" >
      <XMI.header>
        <XMI.documentation>
          <XMI.exporter>dbdoclet http://www.dbdoclet.org</XMI.exporter>
          <XMI.exporterVersion>0.61.0</XMI.exporterVersion>
          <XMI.exporterEncoding>UnicodeUTF8</XMI.exporterEncoding>
        </XMI.documentation>
      </XMI.header>
      <XMI.content>
        <UML:Model>
          <UML:DataType stereotype="42" visibility="public" xmi.id="1" name="int" />
          <UML:DataType stereotype="42" visibility="public" xmi.id="2" name="char" />
          <UML:DataType stereotype="42" visibility="public" xmi.id="3" name="bool" />
          <UML:DataType stereotype="42" visibility="public" xmi.id="4" name="float" />
          <UML:DataType stereotype="42" visibility="public" xmi.id="5" name="double" />
          <UML:DataType stereotype="42" visibility="public" xmi.id="6" name="long" />
          <UML:DataType stereotype="42" visibility="public" xmi.id="7" name="short" />
          <UML:Stereotype visibility="public" xmi.id="20" name="constructor" />
          <UML:Stereotype visibility="public" xmi.id="21" name="interface" />
          <UML:Stereotype visibility="public" xmi.id="22" name="exception" />
          <UML:Stereotype visibility="public" xmi.id="23" name="error" />
          <xsl:apply-templates/>
        </UML:Model>
      </XMI.content>
    </XMI>
  </xsl:template>

  <xsl:template match="package">
    <UML:Package>
      <xsl:attribute name="visibility">public</xsl:attribute>
      <xsl:attribute name="name"><xsl:value-of select="@name"/></xsl:attribute>
      <xsl:attribute name="xmi.id"><xsl:value-of select="substring-after(@id, 'dbdoclet.')"/></xsl:attribute>
      <xsl:apply-templates/>
    </UML:Package>
  </xsl:template>

  <xsl:template match="class">
    <UML:Class>
      <xsl:attribute name="visibility"><xsl:value-of select="@visibility"/></xsl:attribute>
      <xsl:attribute name="name"><xsl:value-of select="@name"/></xsl:attribute>
      <xsl:attribute name="xmi.id"><xsl:value-of select="substring-after(@id, 'dbdoclet.')"/></xsl:attribute>
      <xsl:if test="@isAbstract = 'true'">
        <xsl:attribute name="isAbstract"><xsl:value-of select="@isAbstract"/></xsl:attribute>
      </xsl:if>
      <xsl:if test="@isInterface = 'true'">
        <xsl:attribute name="stereotype">21</xsl:attribute>
      </xsl:if>
      <xsl:if test="@isException = 'true'">
        <xsl:attribute name="stereotype">22</xsl:attribute>
      </xsl:if>
      <xsl:apply-templates/>
    </UML:Class>
  </xsl:template>

  <xsl:template match="constructor">
    <UML:Operation>
      <xsl:attribute name="name"><xsl:value-of select="@name"/></xsl:attribute>
      <xsl:attribute name="type">void</xsl:attribute>
      <xsl:attribute name="visibility"><xsl:value-of select="@visibility"/></xsl:attribute>
      <xsl:attribute name="xmi.id"><xsl:value-of select="substring-after(@id, 'dbdoclet.')"/></xsl:attribute>
      <xsl:apply-templates/>
    </UML:Operation>
  </xsl:template>

  <xsl:template match="field">
    <UML:Attribute>
      <xsl:attribute name="name"><xsl:value-of select="@name"/></xsl:attribute>
      <xsl:attribute name="type"><xsl:value-of select="@type"/></xsl:attribute>
      <xsl:attribute name="visibility"><xsl:value-of select="@visibility"/></xsl:attribute>
      <xsl:attribute name="xmi.id"><xsl:value-of select="substring-after(@id, 'dbdoclet.')"/></xsl:attribute>
      <xsl:if test="@isStatic = 'true'">
        <xsl:attribute name="ownerScope">classifier</xsl:attribute>
      </xsl:if>
      <xsl:apply-templates/>
    </UML:Attribute>
  </xsl:template>

  <xsl:template match="method">
    <UML:Operation>
      <xsl:attribute name="name"><xsl:value-of select="@name"/></xsl:attribute>
      <xsl:attribute name="type"><xsl:value-of select="@type"/></xsl:attribute>
      <xsl:attribute name="visibility"><xsl:value-of select="@visibility"/></xsl:attribute>
      <xsl:attribute name="xmi.id"><xsl:value-of select="substring-after(@id, 'dbdoclet.')"/></xsl:attribute>
      <xsl:if test="@isAbstract = 'true'">
        <xsl:attribute name="isAbstract"><xsl:value-of select="@isAbstract"/></xsl:attribute>
      </xsl:if>
      <xsl:if test="@isStatic = 'true'">
        <xsl:attribute name="ownerScope">classifier</xsl:attribute>
      </xsl:if>
      <xsl:apply-templates/>
    </UML:Operation>
  </xsl:template>

  <xsl:template match="parameter">
    <UML:Parameter>
      <xsl:attribute name="name"><xsl:value-of select="@name"/></xsl:attribute>
      <xsl:attribute name="type"><xsl:value-of select="@type"/></xsl:attribute>
      <xsl:attribute name="xmi.id"><xsl:value-of select="substring-after(@id, 'dbdoclet.')"/></xsl:attribute>
      <xsl:apply-templates/>
    </UML:Parameter>

  </xsl:template>

  <xsl:template match="generalization">
    <UML:Generalization>
      <xsl:attribute name="visibility">public</xsl:attribute>
      <xsl:attribute name="child"><xsl:value-of select="@child"/></xsl:attribute>
      <xsl:attribute name="parent"><xsl:value-of select="@parent"/></xsl:attribute>
      <xsl:attribute name="xmi.id"><xsl:value-of select="substring-after(@id, 'dbdoclet.')"/></xsl:attribute>
      <xsl:attribute name="name"><xsl:value-of select="@name"/></xsl:attribute>
    </UML:Generalization>
  </xsl:template>

  <xsl:template match="association">
    <UML:Association>
      <xsl:attribute name="visibility">public</xsl:attribute>
      <xsl:attribute name="xmi.id"><xsl:value-of select="substring-after(@id, 'dbdoclet.')"/></xsl:attribute>
      <UML:Association.connection>
        <UML:AssociationEndRole>
          <xsl:attribute name="visibility">public</xsl:attribute>
          <xsl:attribute name="aggregation"><xsl:value-of select="@type"/></xsl:attribute>
          <xsl:attribute name="type"><xsl:value-of select="@aggregate"/></xsl:attribute>
          <xsl:attribute name="isNavigable"><xsl:value-of select="@isAggregateNavigable"/></xsl:attribute>
        </UML:AssociationEndRole>
        <UML:AssociationEndRole>
          <xsl:attribute name="visibility">public</xsl:attribute>
          <xsl:attribute name="type"><xsl:value-of select="@part"/></xsl:attribute>
          <xsl:attribute name="isNavigable"><xsl:value-of select="@isPartNavigable"/></xsl:attribute>
        </UML:AssociationEndRole>
      </UML:Association.connection>
    </UML:Association>
  </xsl:template>

</xsl:stylesheet>
