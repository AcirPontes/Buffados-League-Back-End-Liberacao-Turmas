(function ($, window, document) {
    'use strict';

    const subjectSuggestions = [
        'Arte',
        'Biologia',
        'Educação Física',
        'Filosofia',
        'Física',
        'Geografia',
        'História',
        'Língua Inglesa',
        'Língua Portuguesa',
        'Matemática',
        'Química',
        'Sociologia',
        'Programação Web',
        'Banco de Dados',
        'Desenvolvimento de Sistemas',
        'Eletrônica Digital',
        'Instalações Elétricas',
        'Topografia',
        'Desenho Técnico',
        'Eventos',
        'Técnicas Secretariais'
    ];

    function initializeDatepicker() {
        if (!$.datepicker) {
            return;
        }

        $.datepicker.regional['pt-BR'] = {
            closeText: 'Fechar',
            prevText: 'Anterior',
            nextText: 'Próximo',
            currentText: 'Hoje',
            monthNames: [
                'Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho',
                'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro'
            ],
            monthNamesShort: [
                'Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun',
                'Jul', 'Ago', 'Set', 'Out', 'Nov', 'Dez'
            ],
            dayNames: [
                'Domingo', 'Segunda-feira', 'Terça-feira', 'Quarta-feira',
                'Quinta-feira', 'Sexta-feira', 'Sábado'
            ],
            dayNamesShort: ['Dom', 'Seg', 'Ter', 'Qua', 'Qui', 'Sex', 'Sáb'],
            dayNamesMin: ['Do', 'Se', 'Te', 'Qua', 'Qui', 'Sex', 'Sá'],
            weekHeader: 'Sm',
            dateFormat: 'dd/mm/yy',
            firstDay: 0,
            isRTL: false,
            showMonthAfterYear: false,
            yearSuffix: ''
        };

        $.datepicker.setDefaults($.datepicker.regional['pt-BR']);
        $('.js-datepicker').each(function () {
            $(this).datepicker({
                minDate: 0,
                changeMonth: true,
                changeYear: true,
                yearRange: 'c:c+2',
                showOtherMonths: true,
                selectOtherMonths: true
            });
        });
    }

    function initializeSidebar() {
        const $sidebar = $('.sidebar');
        const $scrim = $('[data-sidebar-scrim]');

        function closeSidebar() {
            $sidebar.removeClass('sidebar-open');
            $scrim.removeClass('is-visible');
        }

        $('[data-sidebar-toggle]').on('click', function () {
            $sidebar.toggleClass('sidebar-open');
            $scrim.toggleClass('is-visible', $sidebar.hasClass('sidebar-open'));
        });

        $scrim.on('click', closeSidebar);
        $(document).on('keydown', function (event) {
            if (event.key === 'Escape') {
                closeSidebar();
            }
        });
    }

    function initializeFlashMessages() {
        $('.flash').each(function () {
            const $message = $(this);
            window.setTimeout(function () {
                $message.fadeTo(250, 0, function () {
                    $(this).slideUp(180);
                });
            }, 7500);
        });
    }

    function initializePasswordVisibility() {
        $('[data-password-toggle]').on('click', function () {
            const inputId = $(this).attr('data-password-toggle');
            const $input = $('#' + inputId);
            const isPassword = $input.attr('type') === 'password';

            $input.attr('type', isPassword ? 'text' : 'password');
            $(this).text(isPassword ? 'Ocultar' : 'Mostrar');
            $(this).attr('aria-label', isPassword ? 'Ocultar senha' : 'Mostrar senha');
        });
    }

    function passwordStrength(password) {
        let score = 0;

        if (password.length >= 8) score += 1;
        if (password.length >= 12) score += 1;
        if (/[A-Z]/.test(password) && /[a-z]/.test(password)) score += 1;
        if (/[0-9]/.test(password)) score += 1;
        if (/[^A-Za-z0-9]/.test(password)) score += 1;

        return Math.min(score, 5);
    }

    function initializePasswordMeter() {
        const $password = $('#password');
        const $meter = $('[data-password-meter]');

        if (!$password.length || !$meter.length) {
            return;
        }

        $password.on('input', function () {
            const strength = passwordStrength($(this).val());
            const percentage = strength * 20;
            const tone = strength <= 2 ? '#d9a341' : (strength <= 4 ? '#4d9bb4' : '#23945e');

            $meter.css({ width: percentage + '%', backgroundColor: tone });
        });
    }

    function initializeSubjectAutocomplete() {
        if (!$.ui || !$.ui.autocomplete) {
            return;
        }

        $('.js-subject-autocomplete').autocomplete({
            source: subjectSuggestions,
            minLength: 2,
            delay: 120,
            select: function (event, ui) {
                const $input = $(this);
                const currentValues = $input.val().split(',').map(value => value.trim()).filter(Boolean);

                if (!currentValues.includes(ui.item.value)) {
                    currentValues.push(ui.item.value);
                    $input.val(currentValues.join(', '));
                }

                return false;
            }
        });
    }

    function initializePetitionForm() {
        const $form = $('[data-petition-form]');

        if (!$form.length) {
            return;
        }

        const $firstPeriod = $('#firstPeriod');
        const $lastPeriod = $('#lastPeriod');
        const $reason = $('#reason');
        const $counter = $('[data-char-count]');
        const $submitButton = $('[data-submit-petition]');
        const $includeDabc = $('#includeDabc');
        const $routingSummary = $('[data-routing-summary]');

        function validatePeriods() {
            const first = Number($firstPeriod.val());
            const last = Number($lastPeriod.val());
            const isInvalid = first && last && first > last;

            $lastPeriod[0].setCustomValidity(isInvalid
                ? 'O último horário não pode ser anterior ao primeiro.'
                : '');

            $('[data-period-hint]').text(isInvalid
                ? 'Atenção: o último horário deve ser igual ou posterior ao primeiro.'
                : 'O intervalo será conferido antes do protocolo.');
        }

        function updateCharacterCount() {
            $counter.text(($reason.val() || '').length);
        }

        function updateRoutingSummary() {
            if (!$includeDabc.length || !$routingSummary.length) {
                return;
            }

            $routingSummary.text($includeDabc.is(':checked')
                ? 'O departamento do curso e o DABC receberão a petição para análise.'
                : 'A petição será encaminhada ao departamento do seu curso. Marque a opção acima para incluir o DABC.');
        }

        $firstPeriod.add($lastPeriod).on('change', validatePeriods);
        $reason.on('input', updateCharacterCount);
        $includeDabc.on('change', updateRoutingSummary);
        updateCharacterCount();
        validatePeriods();
        updateRoutingSummary();

        $form.on('submit', function (event) {
            validatePeriods();
            if (!this.checkValidity()) {
                event.preventDefault();
                this.reportValidity();
                return;
            }

            if ($form.data('submitting')) {
                event.preventDefault();
                return;
            }

            $form.data('submitting', true);
            $submitButton.prop('disabled', true).addClass('button-loading');
            $submitButton.attr('data-original-text', $submitButton.text());
            $submitButton.text('Enviando petição...');
        });
    }

    function initializeTableSearch() {
        $('[data-table-search]').on('input', function () {
            const tableId = $(this).attr('data-table-search');
            const query = $(this).val().toLocaleLowerCase('pt-BR');

            $('#' + tableId + ' tbody tr').each(function () {
                const content = $(this).text().toLocaleLowerCase('pt-BR');
                $(this).toggle(content.includes(query));
            });
        });
    }

    function initializeConfirmations() {
        $(document).on('click', '[data-confirm]', function (event) {
            const trigger = this;
            const $trigger = $(trigger);

            if ($trigger.data('confirmationPassed')) {
                $trigger.removeData('confirmationPassed');
                return;
            }

            event.preventDefault();

            const message = $trigger.attr('data-confirm') || 'Deseja continuar com esta operação?';
            const $dialog = $('<div>', {
                class: 'confirmation-dialog',
                text: message
            }).appendTo('body');

            $dialog.dialog({
                modal: true,
                resizable: false,
                draggable: false,
                width: Math.min(420, $(window).width() - 30),
                title: 'Confirmar operação',
                buttons: [
                    {
                        text: 'Confirmar',
                        click: function () {
                            $(this).dialog('close');
                            $trigger.data('confirmationPassed', true);

                            if (trigger.form && typeof trigger.form.requestSubmit === 'function') {
                                trigger.form.requestSubmit(trigger);
                            } else if (trigger.form) {
                                trigger.form.submit();
                            } else if (trigger.href) {
                                window.location.assign(trigger.href);
                            }
                        }
                    },
                    {
                        text: 'Voltar',
                        click: function () {
                            $(this).dialog('close');
                        }
                    }
                ],
                close: function () {
                    $(this).dialog('destroy').remove();
                }
            });
        });
    }

    function initializeTooltips() {
        if ($.fn.tooltip) {
            $('[title]').tooltip({
                track: true,
                show: { delay: 250 },
                hide: { delay: 80 }
            });
        }
    }

    $(function () {
        initializeDatepicker();
        initializeSidebar();
        initializeFlashMessages();
        initializePasswordVisibility();
        initializePasswordMeter();
        initializeSubjectAutocomplete();
        initializePetitionForm();
        initializeTableSearch();
        initializeConfirmations();
        initializeTooltips();
    });

})(jQuery, window, document);
