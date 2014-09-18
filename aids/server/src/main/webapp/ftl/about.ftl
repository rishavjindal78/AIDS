<#import "common.ftl" as com>
<#escape x as x?html>
    <@com.page title="Version 1.00" activeTab="about">
    <dl>
        <dt>What's Power Saver all about ?</dt>
        <dd>Power Saver an enterprise application aimed to save electricity power by scheduling turning off/on your PC as per your requirement.
            For example suppose you work in a office 8am to 5pm and want Power saver to shutdown your PC at 5:30pm and bring it back up at 7:30am,
            that's quite possible !</dd>
        <br>
        <dt>What all is possible with Power Saver ?</dt>
        <dd>
            <ol>
                <li>Scheduling shutdown of a PC machine</li>
                <li>Scheduling wake-up of a PC machine</li>
                <li>Mass shutdown/restart of enterprise machines</li>
                <li>mass wake-up of enterprise machines</li>
            </ol>
        </dd>
        <dt>Is this Open Source ?</dt>
        <dd>Yes this application is open source aimed for community service, you can run it freely! </dd>
        <br>
        <dt>What technologies used for writing this software ?</dt>
        <dd>Java, Jersey web services, Freemarker, bootstrap css, jquery, JPA. </dd>
        <br>
        <dt>Which all platforms supported by this software ?</dt>
        <dd>All platforms where Java is supported (Linux, Windows, Mac) </dd>
        <br>
        <dt>System requirements</dt>
        <dd>JDK 1.6 or later </dd>
    </dl>
    Logged In User : ${username!}
    </@com.page>
</#escape>