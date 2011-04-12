<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" indent="yes"/>

    <xsl:template match="IWIR">
        <tool>
            <toolname>
                <xsl:value-of select="@wfname"/>
            </toolname>

            <package/>

            <version>
                <xsl:value-of select="@version"/>
            </version>


            <parameters></parameters>


            <inparam></inparam>
            <outparam></outparam>

            <xsl:apply-templates select="blockScope"/>


        </tool>
    </xsl:template>


    <xsl:template match="blockScope">
        <xsl:variable name="groupName" select="@name"/>

        <inportnum>
            <xsl:value-of select="count(inputPort[1])"/>
        </inportnum>
        <outportnum>
            <xsl:value-of select="count(outputPort[1])"/>
        </outportnum>

        <xsl:for-each select="inputPorts">
            <input>
                <xsl:for-each select="inputPort">
                    <xsl:variable name="nodeCounter">
                        <xsl:value-of select="position() -1"/>

                    </xsl:variable>
                    <node index="{$nodeCounter}">
                        <type>java.lang.object<!--<xsl:value-of select="@type"/>--></type>
                    </node>
                </xsl:for-each>
            </input>
        </xsl:for-each>

        <xsl:for-each select="outputPorts">
            <output>
                <xsl:for-each select="outputPort">
                    <xsl:variable name="nodeCounter">
                        <xsl:value-of select="position() -1"/>

                    </xsl:variable>
                    <node index="{$nodeCounter}">
                        <type>java.lang.object<!--<xsl:value-of select="@type"/>--></type>
                    </node>
                </xsl:for-each>
            </output>

        </xsl:for-each>

        <tasks>
            <xsl:apply-templates select="body"/>

            <xsl:apply-templates select="links">
                <xsl:with-param name="groupName" select="$groupName"/>
            </xsl:apply-templates>


        </tasks>

    </xsl:template>


    <xsl:template match="body">
        <xsl:apply-templates select="task"/>
    </xsl:template>


    <xsl:template match="task">
        <task>
            <toolname>
                <xsl:value-of select="@name"/>
            </toolname>
            <package>iwir</package>
            <version>0.1</version>

            <proxy type="Java">
                <param paramname="unitName">
                    <value type="java.lang.String">Task</value>
                </param>
                <param paramname="unitPackage">
                    <value type="java.lang.String">org.trianacode.shiwa.iwirTools</value>
                </param>
            </proxy>
            <renderingHints>
                <renderingHint hint="TaskGraphFactory" proxyDependent="true">
                    <param paramname="factory">
                        <value type="java.lang.String">Default</value>
                    </param>
                </renderingHint>
            </renderingHints>

            <xsl:apply-templates select="inputPorts"/>
            <xsl:apply-templates select="outputPorts"/>


        </task>


    </xsl:template>


    <!--task inputs-->

    <xsl:template match="inputPorts">
        <inportnum>
            <xsl:value-of select="count(inputPort[1])"/>
        </inportnum>

        <input>
            <xsl:for-each select="inputPort">
                <type>
                    <xsl:value-of select="@type"/>
                </type>
            </xsl:for-each>
        </input>
    </xsl:template>


    <!--task outputs-->

    <xsl:template match="outputPorts">
        <outportnum>
            <xsl:value-of select="count(outputPort[1])"/>
        </outportnum>

        <output>
            <xsl:for-each select="outputPort">
                <type>
                    <xsl:value-of select="@type"/>
                </type>
            </xsl:for-each>
        </output>
    </xsl:template>


    <xsl:template match="links">
        <xsl:param name="groupName"/>

        <xsl:for-each select="link">
            <xsl:if test="substring-before(@from, '/') = 't1'">
            </xsl:if>
        </xsl:for-each>


        <!--<xsl:variable name="t1inputs" select="count()"/>-->

        <connections>
            <xsl:for-each select="link">
                <xsl:variable name="sourceTask" select="@from"/>
                <xsl:variable name="sourceTaskName" select="(substring-before($sourceTask, '/'))"/>
                <xsl:variable name="targetTask" select="@to"/>
                <xsl:variable name="targetTaskName" select="(substring-before($targetTask, '/'))"/>
                <xsl:if test="$sourceTaskName != $groupName and $targetTaskName != $groupName">
                    <connection type="NonRunnable">
                        <source node="0" taskname="{$sourceTaskName}"/>
                        <target node="0" taskname="{$targetTaskName}"/>
                    </connection>
                </xsl:if>
            </xsl:for-each>
        </connections>

        <groupnodemapping>
            <input>
                <xsl:for-each select="link">
                    <xsl:variable name="sourceTask" select="@from"/>
                    <xsl:variable name="sourceTaskName" select="(substring-before($sourceTask, '/'))"/>
                    <xsl:variable name="targetTask" select="@to"/>
                    <xsl:variable name="targetTaskName" select="(substring-before($targetTask, '/'))"/>
                    <xsl:if test="$sourceTaskName = $groupName">
                        <node externalnode="0" node="0" taskname="{$targetTaskName}"/>
                    </xsl:if>
                </xsl:for-each>
            </input>
            <output>
                <xsl:for-each select="link">
                    <xsl:variable name="sourceTask" select="@from"/>
                    <xsl:variable name="sourceTaskName" select="(substring-before($sourceTask, '/'))"/>
                    <xsl:variable name="targetTask" select="@to"/>
                    <xsl:variable name="targetTaskName" select="(substring-before($targetTask, '/'))"/>
                    <xsl:if test="$targetTaskName = $groupName">
                        <node externalnode="0" node="0" taskname="{$sourceTaskName}"/>
                    </xsl:if>
                </xsl:for-each>
            </output>
        </groupnodemapping>

    </xsl:template>

</xsl:stylesheet>


