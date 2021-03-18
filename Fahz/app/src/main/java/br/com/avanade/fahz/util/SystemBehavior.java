package br.com.avanade.fahz.util;

import br.com.avanade.fahz.fragments.benefits.DependentBenefitAdhesionFragment;

public class SystemBehavior {
    public enum BehaviorEnum {
        INCL_DEP_CONJ_HEALTH(1),
        INCL_DEP_COMP_HEALTH_BOTH_MARRIGE(2),
        INCL_DEP_COMP_HEALTH_DEP_MARRIGE(3),
        INCL_DEP_SON_HEALTH_ADOP_MAJOR_WORK(4),
        INCL_DEP_SON_HEALTH_ADOP_MAJOR(5),
        INCL_DEP_SON_HEALTH_ADOP_INVALID(6),
        INCL_DEP_SON_HEALTH_MAJOR_WORK(7),
        INCL_DEP_SON_HEALTH_MAJOR(8),
        INCL_DEP_SON_HEALTH_INVALID(9),
        INCL_DEP_CUR_HEALTH_MAJOR_WORK(10),
        INCL_DEP_CUR_HEALTH_MAJOR(11),
        INCL_DEP_CUR_HEALTH_INVALID(12),
        INCL_DEP_GUARD_HEALTH_MAJOR_WORK(13),
        INCL_DEP_GUARD_HEALTH_MAJOR(14),
        INCL_DEP_GUARD_HEALTH_INVALID(15),
        INCL_DEP_ENT_HEALTH_SHARED_MAJOR_WORK(16),
        INCL_DEP_ENT_HEALTH_SHARED_MAJOR(17),
        INCL_DEP_ENT_HEALTH_SHARED_INVALID(18),
        INCL_DEP_ENT_HEALTH_NO_SHARED_MAJOR_WORK(19),
        INCL_DEP_ENT_HEALTH_NO_SHARED_MAJOR(20),
        INCL_DEP_ENT_HEALTH_NO_SHARED_INVALID(21),
        INCL_DEP_ENT_HEALTH_MAJOR_WORK(22),
        INCL_DEP_ENT_HEALTH_MAJOR(23),
        INCL_DEP_ENT_HEALTH_INVALID(24),
        INCL_DEP_DAD_HEALTH(25),
        INCL_DEP_MOM_HEALTH(26),
        INCL_DEP_TUT_HEALTH_MAJOR_WORK(27),
        INCL_DEP_TUT_HEALTH_MAJOR(28),
        INCL_DEP_TUT_HEALTH_INVALID(29),
        INCL_DEP_COMP_HEALTH(30),
        EXCL_DEPENDENT(31),
        INCL_DEP_SON_HEALTH_ADOP_MINOR(32),
        INCL_DEP_SON_HEALTH_MINOR(33),
        INCL_DEP_CUR_HEALTH_MINOR(34),
        INCL_DEP_GUARD_HEALTH_MINOR(35),
        INCL_DEP_ENT_HEALTH_SHARED_MINOR(36),
        INCL_DEP_ENT_HEALTH_NO_SHARED_MINOR(37),
        INCL_DEP_ENT_HEALTH_MINOR(38),
        INCL_DEP_TUT_HEALTH_MINOR(39),
        EDIT_LIFE(40),
        EXCL_LIFE(41),
        CHANGE_HOLDER(42),
        EDIT_DEPENDENT(43),
        RETURN_ABSENSE_LIFE(44),
        ABSENSE_LIFE(45),
        INCL_LIFE_HEALTH(46),
        INCL_LIFE_HEALTH_EXCEPTION(47),
        EXCL_LIFE_HEALTH(48),
        EXCL_DEP_HEALTH(49),
        REACT_LIFE(50),
        REACT_DEPENDENT(51),
        INCL_LIFE(52),
        INCL_DEP_CONJ(53),
        INCL_DEP_COMP_BOTH_MARRIGE(54),
        INCL_DEP_COMP_DEP_MARRIGE(55),
        INCL_DEP_SON_ADOP_MAJOR_WORK(56),
        INCL_DEP_SON_ADOP_MAJOR(57),
        INCL_DEP_SON_ADOP_INVALID(58),
        INCL_DEP_SON_MAJOR_WORK(59),
        INCL_DEP_SON_MAJOR(60),
        INCL_DEP_SON_INVALID(61),
        INCL_DEP_CUR_MAJOR_WORK(62),
        INCL_DEP_CUR_MAJOR(63),
        INCL_DEP_CUR_INVALID(64),
        INCL_DEP_GUARD_MAJOR_WORK(65),
        INCL_DEP_GUARD_MAJOR(66),
        INCL_DEP_GUARD_INVALID(67),
        INCL_DEP_ENT_SHARED_MAJOR_WORK(68),
        INCL_DEP_ENT_SHARED_MAJOR(69),
        INCL_DEP_ENT_SHARED_INVALID(70),
        INCL_DEP_ENT_NO_SHARED_MAJOR_WORK(71),
        INCL_DEP_ENT_NO_SHARED_MAJOR(72),
        INCL_DEP_ENT_NO_SHARED_INVALID(73),
        INCL_DEP_ENT_MAJOR_WORK(74),
        INCL_DEP_ENT_MAJOR(75),
        INCL_DEP_ENT_INVALID(76),
        INCL_DEP_DAD(77),
        INCL_DEP_MOM(78),
        INCL_DEP_TUT_MAJOR_WORK(79),
        INCL_DEP_TUT_MAJOR(80),
        INCL_DEP_TUT_INVALID(81),
        INCL_DEP_COMP(82),
        INCL_DEP_SON_ADOP_MINOR(83),
        INCL_DEP_SON_MINOR(84),
        INCL_DEP_CUR_MINOR(85),
        INCL_DEP_GUARD_MINOR(86),
        INCL_DEP_ENT_SHARED_MINOR(87),
        INCL_DEP_ENT_NO_SHARED_MINOR(88),
        INCL_DEP_ENT_MINOR(89),
        INCL_DEP_TUT_MINOR(90),
        INCL_DEP_COMP_HOLDER_MARRIGE(91),
        INCL_DEP_COMP_HEALTH_HOLDER_MARRIGE(92),
        INCL_DEP_CONJ_DENTAL(93),
        INCL_DEP_COMP_DENTAL_BOTH_MARRIGE(94),
        INCL_DEP_COMP_DENTAL_DEP_MARRIGE(95),
        INCL_DEP_SON_DENTAL_ADOP_MAJOR_WORK(96),
        INCL_DEP_SON_DENTAL_ADOP_MAJOR(97),
        INCL_DEP_SON_DENTAL_ADOP_INVALID(98),
        INCL_DEP_SON_DENTAL_MAJOR_WORK(99),
        INCL_DEP_SON_DENTAL_MAJOR(100),
        INCL_DEP_SON_DENTAL_INVALID(101),
        INCL_DEP_CUR_DENTAL_MAJOR_WORK(102),
        INCL_DEP_CUR_DENTAL_MAJOR(103),
        INCL_DEP_CUR_DENTAL_INVALID(104),
        INCL_DEP_GUARD_DENTAL_MAJOR_WORK(105),
        INCL_DEP_GUARD_DENTAL_MAJOR(106),
        INCL_DEP_GUARD_DENTAL_INVALID(107),
        INCL_DEP_ENT_DENTAL_SHARED_MAJOR_WORK(108),
        INCL_DEP_ENT_DENTAL_SHARED_MAJOR(109),
        INCL_DEP_ENT_DENTAL_SHARED_INVALID(110),
        INCL_DEP_ENT_DENTAL_NO_SHARED_MAJOR_WORK(111),
        INCL_DEP_ENT_DENTAL_NO_SHARED_MAJOR(112),
        INCL_DEP_ENT_DENTAL_NO_SHARED_INVALID(113),
        INCL_DEP_ENT_DENTAL_MAJOR_WORK(114),
        INCL_DEP_ENT_DENTAL_MAJOR(115),
        INCL_DEP_ENT_DENTAL_INVALID(116),
        INCL_DEP_DAD_DENTAL(117),
        INCL_DEP_MOM_DENTAL(118),
        INCL_DEP_TUT_DENTAL_MAJOR_WORK(119),
        INCL_DEP_TUT_DENTAL_MAJOR(120),
        INCL_DEP_TUT_DENTAL_INVALID(121),
        INCL_DEP_COMP_DENTAL(122),
        INCL_DEP_SON_DENTAL_ADOP_MINOR(123),
        INCL_DEP_SON_DENTAL_MINOR(124),
        INCL_DEP_CUR_DENTAL_MINOR(125),
        INCL_DEP_GUARD_DENTAL_MINOR(126),
        INCL_DEP_ENT_DENTAL_SHARED_MINOR(127),
        INCL_DEP_ENT_DENTAL_NO_SHARED_MINOR(128),
        INCL_DEP_ENT_DENTAL_MINOR(129),
        INCL_DEP_TUT_DENTAL_MINOR(130),
        INCL_LIFE_DENTAL(131),
        INCL_LIFE_DENTAL_EXCEPTION(132),
        EXCL_LIFE_DENTAL(133),
        EXCL_DEP_DENTAL(134),
        INCL_DEP_COMP_DENTAL_HOLDER_MARRIGE(135),
        INCL_DEP_CONJ_PHARMACY(136),
        INCL_DEP_COMP_PHARMACY_BOTH_MARRIGE(137),
        INCL_DEP_COMP_PHARMACY_DEP_MARRIGE(138),
        INCL_DEP_SON_PHARMACY_ADOP_MAJOR_WORK(139),
        INCL_DEP_SON_PHARMACY_ADOP_MAJOR(140),
        INCL_DEP_SON_PHARMACY_ADOP_INVALID(141),
        INCL_DEP_SON_PHARMACY_MAJOR_WORK(142),
        INCL_DEP_SON_PHARMACY_MAJOR(143),
        INCL_DEP_SON_PHARMACY_INVALID(144),
        INCL_DEP_CUR_PHARMACY_MAJOR_WORK(145),
        INCL_DEP_CUR_PHARMACY_MAJOR(146),
        INCL_DEP_CUR_PHARMACY_INVALID(147),
        INCL_DEP_GUARD_PHARMACY_MAJOR_WORK(148),
        INCL_DEP_GUARD_PHARMACY_MAJOR(149),
        INCL_DEP_GUARD_PHARMACY_INVALID(150),
        INCL_DEP_ENT_PHARMACY_SHARED_MAJOR_WORK(151),
        INCL_DEP_ENT_PHARMACY_SHARED_MAJOR(152),
        INCL_DEP_ENT_PHARMACY_SHARED_INVALID(153),
        INCL_DEP_ENT_PHARMACY_NO_SHARED_MAJOR_WORK(154),
        INCL_DEP_ENT_PHARMACY_NO_SHARED_MAJOR(155),
        INCL_DEP_ENT_PHARMACY_NO_SHARED_INVALID(156),
        INCL_DEP_ENT_PHARMACY_MAJOR_WORK(157),
        INCL_DEP_ENT_PHARMACY_MAJOR(158),
        INCL_DEP_ENT_PHARMACY_INVALID(159),
        INCL_DEP_DAD_PHARMACY(160),
        INCL_DEP_MOM_PHARMACY(161),
        INCL_DEP_TUT_PHARMACY_MAJOR_WORK(162),
        INCL_DEP_TUT_PHARMACY_MAJOR(163),
        INCL_DEP_TUT_PHARMACY_INVALID(164),
        INCL_DEP_COMP_PHARMACY(165),
        INCL_DEP_SON_PHARMACY_ADOP_MINOR(166),
        INCL_DEP_SON_PHARMACY_MINOR(167),
        INCL_DEP_CUR_PHARMACY_MINOR(168),
        INCL_DEP_GUARD_PHARMACY_MINOR(169),
        INCL_DEP_ENT_PHARMACY_SHARED_MINOR(170),
        INCL_DEP_ENT_PHARMACY_NO_SHARED_MINOR(171),
        INCL_DEP_ENT_PHARMACY_MINOR(172),
        INCL_DEP_TUT_PHARMACY_MINOR(173),
        INCL_LIFE_PHARMACY(174),
        INCL_LIFE_PHARMACY_EXCEPTION(175),
        EXCL_LIFE_PHARMACY(176),
        EXCL_DEP_PHARMACY(177),
        INCL_DEP_COMP_PHARMACY_HOLDER_MARRIGE(178),
        EDIT_LIFE_HEALTH_OPERATOR(179),
        EDIT_LIFE_HEALTH_PLAN(180),
        EDIT_DEP_HEALTH_OPERATOR(181),
        EDIT_DEP_HEALTH_PLAN(182),
        EDIT_LIFE_DENTAL_OPERATOR(183),
        EDIT_LIFE_DENTAL_PLAN(184),
        EDIT_DEP_DENTAL_OPERATOR(185),
        EDIT_DEP_DENTAL_PLAN(186),
        EDIT_LIFE_PHARMACY_OPERATOR(187),
        EDIT_LIFE_PHARMACY_PLAN(188),
        EDIT_DEP_PHARMACY_OPERATOR(189),
        EDIT_DEP_PHARMACY_PLAN(190),
        EDIT_LIFE_HEALTH_OPERATOR_EXCEP(191),
        EDIT_LIFE_DENTAL_OPERATOR_EXCEP(192),
        EDIT_LIFE_PHARMACY_OPERATOR_EXCEP(193),
        EDIT_LIFE_HEALTH_PLAN_PRE_VALID(194),
        EDIT_LIFE_DENTAL_PLAN_PRE_VALID(195),
        EDIT_LIFE_PHARMACY_PLAN_PRE_VALID(196),
        EXTEND_SCHEDULE(197),
        INCL_REFUND_MATERIAL_SCHOOL(198),
        INCL_CARD_MATERIAL_SCHOOL(199),
        INCL_RECEIPT_CARD_MATERIAL_SCHOOL(200),
        INCL_PLAN_MATERIAL_SCHOOL(201),
        EDIT_PLAN_MATERIAL_SCHOOL(202),
        INCL_DEP_SON_ADOP_MAJOR_STU(203),
        INCL_DEP_SON_MAJOR_STU(204),
        INCL_DEP_SON_ADOP_MAJOR_WORK_STU(205),
        INCL_DEP_SON_MAJOR_WORK_STU(206),
        INCL_DEP_CUR_MAJOR_STU(207),
        INCL_DEP_CUR_MAJOR_WORK_STU(208),
        INCL_DEP_GUARD_MAJOR_STU(209),
        INCL_DEP_GUARD_MAJOR_WORK_STU(210),
        INCL_DEP_ENT_MAJOR_STU(211),
        INCL_DEP_ENT_MAJOR_WORK_STU(212),
        INCL_DEP_ENT_SHARED_MAJOR_STU(213),
        INCL_DEP_ENT_SHARED_MAJOR_WORK_STU(214),
        INCL_DEP_ENT_NO_SHARED_MAJOR_STU(215),
        INCL_DEP_ENT_NO_SHARED_MAJOR_WORK_STU(216),
        INCL_DEP_TUT_MAJOR_STU(217),
        INCL_DEP_TUT_MAJOR_WORK_STU(218),
        INCL_DEP_SON_HEALTH_ADOP_MAJOR_STU(219),
        INCL_DEP_SON_HEALTH_MAJOR_STU(220),
        INCL_DEP_SON_HEALTH_ADOP_MAJOR_WORK_STU(221),
        INCL_DEP_SON_HEALTH_MAJOR_WORK_STU(222),
        INCL_DEP_CUR_HEALTH_MAJOR_STU(223),
        INCL_DEP_CUR_HEALTH_MAJOR_WORK_STU(224),
        INCL_DEP_GUARD_HEALTH_MAJOR_STU(225),
        INCL_DEP_GUARD_HEALTH_MAJOR_WORK_STU(226),
        INCL_DEP_ENT_HEALTH_MAJOR_STU(227),
        INCL_DEP_ENT_HEALTH_MAJOR_WORK_STU(228),
        INCL_DEP_ENT_HEALTH_SHARED_MAJOR_STU(229),
        INCL_DEP_ENT_HEALTH_SHARED_MAJOR_WORK_STU(230),
        INCL_DEP_ENT_HEALTH_NO_SHARED_MAJOR_STU(231),
        INCL_DEP_ENT_HEALTH_NO_SHARED_MAJOR_WORK_STU(232),
        INCL_DEP_TUT_HEALTH_MAJOR_STU(233),
        INCL_DEP_TUT_HEALTH_MAJOR_WORK_STU(234),
        INCL_DEP_SON_DENTAL_ADOP_MAJOR_STU(235),
        INCL_DEP_SON_DENTAL_MAJOR_STU(236),
        INCL_DEP_SON_DENTAL_ADOP_MAJOR_WORK_STU(237),
        INCL_DEP_SON_DENTAL_MAJOR_WORK_STU(238),
        INCL_DEP_CUR_DENTAL_MAJOR_STU(239),
        INCL_DEP_CUR_DENTAL_MAJOR_WORK_STU(240),
        INCL_DEP_GUARD_DENTAL_MAJOR_STU(241),
        INCL_DEP_GUARD_DENTAL_MAJOR_WORK_STU(242),
        INCL_DEP_ENT_DENTAL_MAJOR_STU(243),
        INCL_DEP_ENT_DENTAL_MAJOR_WORK_STU(244),
        INCL_DEP_ENT_DENTAL_SHARED_MAJOR_STU(245),
        INCL_DEP_ENT_DENTAL_SHARED_MAJOR_WORK_STU(246),
        INCL_DEP_ENT_DENTAL_NO_SHARED_MAJOR_STU(247),
        INCL_DEP_ENT_DENTAL_NO_SHARED_MAJOR_WORK_STU(248),
        INCL_DEP_TUT_DENTAL_MAJOR_STU(249),
        INCL_DEP_TUT_DENTAL_MAJOR_WORK_STU(250),
        INCL_DEP_SON_PHARMACY_ADOP_MAJOR_STU(251),
        INCL_DEP_SON_PHARMACY_MAJOR_STU(252),
        INCL_DEP_SON_PHARMACY_ADOP_MAJOR_WORK_STU(253),
        INCL_DEP_SON_PHARMACY_MAJOR_WORK_STU(254),
        INCL_DEP_CUR_PHARMACY_MAJOR_STU(255),
        INCL_DEP_CUR_PHARMACY_MAJOR_WORK_STU(256),
        INCL_DEP_GUARD_PHARMACY_MAJOR_STU(257),
        INCL_DEP_GUARD_PHARMACY_MAJOR_WORK_STU(258),
        INCL_DEP_ENT_PHARMACY_MAJOR_STU(259),
        INCL_DEP_ENT_PHARMACY_MAJOR_WORK_STU(260),
        INCL_DEP_ENT_PHARMACY_SHARED_MAJOR_STU(261),
        INCL_DEP_ENT_PHARMACY_SHARED_MAJOR_WORK_STU(262),
        INCL_DEP_ENT_PHARMACY_NO_SHARED_MAJOR_STU(263),
        INCL_DEP_ENT_PHARMACY_NO_SHARED_MAJOR_WORK_STU(264),
        INCL_DEP_TUT_PHARMACY_MAJOR_STU(265),
        INCL_DEP_TUT_PHARMACY_MAJOR_WORK_STU(266),
        INCL_DEP_SON_ADOP_MAJOR_FAHZ(267),
        INCL_DEP_SON_MAJOR_FAHZ(268),
        INCL_DEP_CUR_MAJOR_FAHZ(269),
        INCL_DEP_GUARD_MAJOR_FAHZ(270),
        INCL_DEP_ENT_SHARED_MAJOR_FAHZ(271),
        INCL_DEP_ENT_NO_SHARED_MAJOR_FAHZ(272),
        INCL_DEP_ENT_MAJOR_FAHZ(273),
        INCL_DEP_TUT_MAJOR_FAHZ(274),
        INCL_DEP_SON_HEALTH_ADOP_MAJOR_FAHZ(275),
        INCL_DEP_SON_HEALTH_MAJOR_FAHZ(276),
        INCL_DEP_CUR_HEALTH_MAJOR_FAHZ(277),
        INCL_DEP_GUARD_HEALTH_MAJOR_FAHZ(278),
        INCL_DEP_ENT_HEALTH_SHARED_MAJOR_FAHZ(279),
        INCL_DEP_ENT_HEALTH_NO_SHARED_MAJOR_FAHZ(280),
        INCL_DEP_ENT_HEALTH_MAJOR_FAHZ(281),
        INCL_DEP_TUT_HEALTH_MAJOR_FAHZ(282),
        INCL_DEP_SON_DENTAL_ADOP_MAJOR_FAHZ(283),
        INCL_DEP_SON_DENTAL_MAJOR_FAHZ(284),
        INCL_DEP_CUR_DENTAL_MAJOR_FAHZ(285),
        INCL_DEP_GUARD_DENTAL_MAJOR_FAHZ(286),
        INCL_DEP_ENT_DENTAL_SHARED_MAJOR_FAHZ(287),
        INCL_DEP_ENT_DENTAL_NO_SHARED_MAJOR_FAHZ(288),
        INCL_DEP_ENT_DENTAL_MAJOR_FAHZ(289),
        INCL_DEP_TUT_DENTAL_MAJOR_FAHZ(290),
        INCL_DEP_SON_PHARMACY_ADOP_MAJOR_FAHZ(291),
        INCL_DEP_SON_PHARMACY_MAJOR_FAHZ(292),
        INCL_DEP_CUR_PHARMACY_MAJOR_FAHZ(293),
        INCL_DEP_GUARD_PHARMACY_MAJOR_FAHZ(294),
        INCL_DEP_ENT_PHARMACY_SHARED_MAJOR_FAHZ(295),
        INCL_DEP_ENT_PHARMACY_NO_SHARED_MAJOR_FAHZ(296),
        INCL_DEP_ENT_PHARMACY_MAJOR_FAHZ(297),
        INCL_DEP_TUT_PHARMACY_MAJOR_FAHZ(298),
        EXCL_SCHOLARSHIP_FINANCIAL_PLAN_ITEM(299),
        INCL_SCHOLARSHIP_MAINTENANCE(300),
        INCL_SCHOLARSHIP_FINANCIAL_PLAN(301),
        INCL_SCHOLARSHIP_ADD_REFUND(302),
        INCL_TOY_PLAN(308),
        EDIT_TOY_PLAN(309),
        INCL_TOY(310),
        EDIT_TOY(311),
        EXCL_TOY(312),
        INCL_DEP_ESP_TOY(313),
        INCL_SCHOLARSHIP_GRADUATION(346),
        INCL_SCHOLARSHIP_TECHNICAL(347),
        INCL_SCHOLARSHIP_POSTGRADUATE(348),
        INCL_SCHOLARSHIP_MASTERS_DEGREE(349),
        INCL_SCHOLARSHIP_DOCTORATE_DEGREE(350),
        INCL_SCHOLARSHIP_MBA_OTHERS (351),
        INCL_SCHOLARSHIP_EDUCARE (352),

        EDIT_SCHOLARSHIP_GRADUATION(353),
        EDIT_SCHOLARSHIP_TECHNICAL (354),
        EDIT_SCHOLARSHIP_POSTGRADUATE (355),
        EDIT_SCHOLARSHIP_MASTERS_DEGREE (356),
        EDIT_SCHOLARSHIP_DOCTORATE_DEGREE (357),
        EDIT_SCHOLARSHIP_MBA_OTHERS(358),
        EDIT_SCHOLARSHIP_EDUCARE(359),
        INCL_CARD_MATERIAL_SCHOOL_MINOR_HOLDER(373);

        private int mValue;
        BehaviorEnum(int value) { this.mValue = value;} // Constructor
        public int id(){return mValue;}                  // Return enum index

        public static BehaviorEnum fromId(int value) {
            for(BehaviorEnum v : values()) {
                if (v.mValue == value) {
                    return v;
                }
            }
            return null;
        }
    }

    public static BehaviorEnum verifyHealthBehavior(BehaviorEnum SystemBehavior) {

        BehaviorEnum enumValue = null;
        switch (SystemBehavior) {
            case INCL_DEP_CONJ:
                enumValue = BehaviorEnum.INCL_DEP_CONJ_HEALTH;
                DependentBenefitAdhesionFragment.codeTerm = null;
                break;
            case INCL_DEP_COMP_BOTH_MARRIGE:
                enumValue = BehaviorEnum.INCL_DEP_COMP_HEALTH_BOTH_MARRIGE;
                break;
            case INCL_DEP_COMP_DEP_MARRIGE:
                enumValue = BehaviorEnum.INCL_DEP_COMP_HEALTH_DEP_MARRIGE;
                break;
            case INCL_DEP_SON_ADOP_MAJOR_WORK:
                enumValue = BehaviorEnum.INCL_DEP_SON_HEALTH_ADOP_MAJOR_WORK;
                break;
            case INCL_DEP_SON_ADOP_MAJOR:
                enumValue = BehaviorEnum.INCL_DEP_SON_HEALTH_ADOP_MAJOR;
            break;
            case INCL_DEP_SON_ADOP_INVALID:
                enumValue = BehaviorEnum.INCL_DEP_SON_HEALTH_ADOP_INVALID;
            break;
            case INCL_DEP_SON_MAJOR_WORK:
                enumValue = BehaviorEnum.INCL_DEP_SON_HEALTH_MAJOR_WORK;
            break;
            case INCL_DEP_SON_MAJOR:
                enumValue = BehaviorEnum.INCL_DEP_SON_HEALTH_MAJOR;
            break;
            case INCL_DEP_SON_INVALID:
                enumValue = BehaviorEnum.INCL_DEP_SON_HEALTH_INVALID;
            break;
            case INCL_DEP_SON_ADOP_MAJOR_STU:
                enumValue = BehaviorEnum.INCL_DEP_SON_HEALTH_ADOP_MAJOR_STU;
                break;
            case INCL_DEP_SON_MAJOR_STU:
                enumValue = BehaviorEnum.INCL_DEP_SON_HEALTH_MAJOR_STU;
                DependentBenefitAdhesionFragment.codeTerm = Constants.TERMS_OF_ACTIVATION_PLAN_HEALTH_MAJOR;
                break;
            case INCL_DEP_SON_ADOP_MAJOR_WORK_STU:
                enumValue = BehaviorEnum.INCL_DEP_SON_HEALTH_ADOP_MAJOR_WORK_STU;
                break;
            case INCL_DEP_SON_MAJOR_WORK_STU:
                enumValue = BehaviorEnum.INCL_DEP_SON_HEALTH_MAJOR_WORK_STU;
                break;
            case INCL_DEP_CUR_MAJOR_WORK:
                enumValue = BehaviorEnum.INCL_DEP_CUR_HEALTH_MAJOR_WORK;
            break;
            case INCL_DEP_CUR_MAJOR:
                enumValue = BehaviorEnum.INCL_DEP_CUR_HEALTH_MAJOR;
            break;
            case INCL_DEP_CUR_INVALID:
                enumValue = BehaviorEnum.INCL_DEP_CUR_HEALTH_INVALID;
            break;
            case INCL_DEP_CUR_MAJOR_STU:
                enumValue = BehaviorEnum.INCL_DEP_CUR_HEALTH_MAJOR_STU;
                break;
            case INCL_DEP_CUR_MAJOR_WORK_STU:
                enumValue = BehaviorEnum.INCL_DEP_CUR_HEALTH_MAJOR_WORK_STU;
                break;
            case INCL_DEP_GUARD_MAJOR_WORK:
                enumValue = BehaviorEnum.INCL_DEP_GUARD_HEALTH_MAJOR_WORK;
            break;
            case INCL_DEP_GUARD_MAJOR:
                enumValue = BehaviorEnum.INCL_DEP_GUARD_HEALTH_MAJOR;
            break;
            case INCL_DEP_GUARD_INVALID:
                enumValue = BehaviorEnum.INCL_DEP_GUARD_HEALTH_INVALID;
            break;
            case INCL_DEP_GUARD_MAJOR_STU:
                enumValue = BehaviorEnum.INCL_DEP_GUARD_HEALTH_MAJOR_STU;
                break;
            case INCL_DEP_GUARD_MAJOR_WORK_STU:
                enumValue = BehaviorEnum.INCL_DEP_GUARD_HEALTH_MAJOR_WORK_STU;
                break;
            case INCL_DEP_ENT_SHARED_MAJOR_WORK:
                enumValue = BehaviorEnum.INCL_DEP_ENT_HEALTH_SHARED_MAJOR_WORK;
            break;
            case INCL_DEP_ENT_SHARED_MAJOR:
                enumValue = BehaviorEnum.INCL_DEP_ENT_HEALTH_SHARED_MAJOR;
            break;
            case INCL_DEP_ENT_SHARED_INVALID:
                enumValue = BehaviorEnum.INCL_DEP_ENT_HEALTH_SHARED_INVALID;
            break;
            case INCL_DEP_ENT_NO_SHARED_MAJOR_WORK:
                enumValue = BehaviorEnum.INCL_DEP_ENT_HEALTH_NO_SHARED_MAJOR_WORK;
            break;
            case INCL_DEP_ENT_NO_SHARED_MAJOR:
                enumValue = BehaviorEnum.INCL_DEP_ENT_HEALTH_NO_SHARED_MAJOR;
            break;
            case INCL_DEP_ENT_NO_SHARED_INVALID:
                enumValue = BehaviorEnum.INCL_DEP_ENT_HEALTH_NO_SHARED_INVALID;
                break;
            case INCL_DEP_ENT_MAJOR_WORK:
                enumValue = BehaviorEnum.INCL_DEP_ENT_HEALTH_MAJOR_WORK;
                break;
            case INCL_DEP_ENT_MAJOR:
                enumValue = BehaviorEnum.INCL_DEP_ENT_HEALTH_MAJOR;
                break;
            case INCL_DEP_ENT_INVALID:
                enumValue = BehaviorEnum.INCL_DEP_ENT_HEALTH_INVALID;
                DependentBenefitAdhesionFragment.codeTerm = Constants.TERMS_OF_ACTIVATION_PLAN_HEALTH_MINOR;
                break;
            case INCL_DEP_ENT_MAJOR_STU:
                enumValue = BehaviorEnum.INCL_DEP_ENT_HEALTH_MAJOR_STU;
                break;
            case INCL_DEP_ENT_MAJOR_WORK_STU:
                enumValue = BehaviorEnum.INCL_DEP_ENT_HEALTH_MAJOR_WORK_STU;
                break;
            case INCL_DEP_ENT_SHARED_MAJOR_STU:
                enumValue = BehaviorEnum.INCL_DEP_ENT_HEALTH_SHARED_MAJOR_STU;
                break;
            case INCL_DEP_ENT_SHARED_MAJOR_WORK_STU:
                enumValue = BehaviorEnum.INCL_DEP_ENT_HEALTH_SHARED_MAJOR_WORK_STU;
                break;
            case INCL_DEP_ENT_NO_SHARED_MAJOR_STU:
                enumValue = BehaviorEnum.INCL_DEP_ENT_HEALTH_NO_SHARED_MAJOR_STU;
                break;
            case INCL_DEP_ENT_NO_SHARED_MAJOR_WORK_STU:
                enumValue = BehaviorEnum.INCL_DEP_ENT_HEALTH_NO_SHARED_MAJOR_WORK_STU;
                break;
            case INCL_DEP_DAD:
                enumValue = BehaviorEnum.INCL_DEP_DAD_HEALTH;
            break;
            case INCL_DEP_MOM:
                enumValue = BehaviorEnum.INCL_DEP_MOM_HEALTH;
            break;
            case INCL_DEP_TUT_MAJOR_WORK:
                enumValue = BehaviorEnum.INCL_DEP_TUT_HEALTH_MAJOR_WORK;
            break;
            case INCL_DEP_TUT_MAJOR:
                enumValue = BehaviorEnum.INCL_DEP_TUT_HEALTH_MAJOR;
            break;
            case INCL_DEP_TUT_INVALID:
                enumValue = BehaviorEnum.INCL_DEP_TUT_HEALTH_INVALID;
            break;
            case INCL_DEP_TUT_MAJOR_STU:
                enumValue = BehaviorEnum.INCL_DEP_TUT_HEALTH_MAJOR_STU;
                break;
            case INCL_DEP_TUT_MAJOR_WORK_STU:
                enumValue = BehaviorEnum.INCL_DEP_TUT_HEALTH_MAJOR_WORK_STU;
                break;
            case INCL_DEP_COMP:
                enumValue = BehaviorEnum.INCL_DEP_COMP_HEALTH;
            break;
            case INCL_DEP_SON_ADOP_MINOR:
                enumValue = BehaviorEnum.INCL_DEP_SON_HEALTH_ADOP_MINOR;
            break;
            case INCL_DEP_SON_MINOR:
                enumValue = BehaviorEnum.INCL_DEP_SON_HEALTH_MINOR;
            break;
            case INCL_DEP_CUR_MINOR:
                enumValue = BehaviorEnum.INCL_DEP_CUR_HEALTH_MINOR;
            break;
            case INCL_DEP_GUARD_MINOR:
                enumValue = BehaviorEnum.INCL_DEP_GUARD_HEALTH_MINOR;
            break;
            case INCL_DEP_ENT_SHARED_MINOR:
                enumValue = BehaviorEnum.INCL_DEP_ENT_HEALTH_SHARED_MINOR;
            break;
            case INCL_DEP_ENT_NO_SHARED_MINOR:
                enumValue = BehaviorEnum.INCL_DEP_ENT_HEALTH_NO_SHARED_MINOR;
            break;
            case INCL_DEP_ENT_MINOR:
                enumValue = BehaviorEnum.INCL_DEP_ENT_HEALTH_MINOR;
            break;
            case INCL_DEP_TUT_MINOR:
                enumValue = BehaviorEnum.INCL_DEP_TUT_HEALTH_MINOR;
            break;
            case INCL_DEP_COMP_HOLDER_MARRIGE:
                enumValue = BehaviorEnum.INCL_DEP_COMP_HEALTH_HOLDER_MARRIGE;
            break;
            case INCL_DEP_SON_ADOP_MAJOR_FAHZ:
                enumValue = BehaviorEnum.INCL_DEP_SON_HEALTH_ADOP_MAJOR_FAHZ;
                break;
            case INCL_DEP_SON_MAJOR_FAHZ:
                enumValue = BehaviorEnum.INCL_DEP_SON_HEALTH_MAJOR_FAHZ;
                break;
            case INCL_DEP_CUR_MAJOR_FAHZ:
                enumValue = BehaviorEnum.INCL_DEP_CUR_HEALTH_MAJOR_FAHZ;
                break;
            case INCL_DEP_GUARD_MAJOR_FAHZ:
                enumValue = BehaviorEnum.INCL_DEP_GUARD_HEALTH_MAJOR_FAHZ;
                break;
            case INCL_DEP_ENT_SHARED_MAJOR_FAHZ:
                enumValue = BehaviorEnum.INCL_DEP_ENT_HEALTH_SHARED_MAJOR_FAHZ;
                break;
            case INCL_DEP_ENT_NO_SHARED_MAJOR_FAHZ:
                enumValue = BehaviorEnum.INCL_DEP_ENT_HEALTH_NO_SHARED_MAJOR_FAHZ;
                break;
            case INCL_DEP_ENT_MAJOR_FAHZ:
                enumValue = BehaviorEnum.INCL_DEP_ENT_HEALTH_MAJOR_FAHZ;
                break;
            case INCL_DEP_TUT_MAJOR_FAHZ:
                enumValue = BehaviorEnum.INCL_DEP_TUT_HEALTH_MAJOR_FAHZ;
                break;
        }

        return enumValue;
    }

    public static BehaviorEnum verifyDentalBehavior(BehaviorEnum SystemBehavior) {

        BehaviorEnum enumValue = null;
        switch (SystemBehavior) {
            case INCL_DEP_CONJ:
                enumValue = BehaviorEnum.INCL_DEP_CONJ_DENTAL;
                DependentBenefitAdhesionFragment.codeTerm = null;
                break;
            case INCL_DEP_COMP_BOTH_MARRIGE:
                enumValue = BehaviorEnum.INCL_DEP_COMP_DENTAL_BOTH_MARRIGE;
                break;
            case INCL_DEP_COMP_DEP_MARRIGE:
                enumValue = BehaviorEnum.INCL_DEP_COMP_DENTAL_DEP_MARRIGE;
                break;
            case INCL_DEP_SON_ADOP_MAJOR_WORK:
                enumValue = BehaviorEnum.INCL_DEP_SON_DENTAL_ADOP_MAJOR_WORK;
                break;
            case INCL_DEP_SON_ADOP_MAJOR:
                enumValue = BehaviorEnum.INCL_DEP_SON_DENTAL_ADOP_MAJOR;
                break;
            case INCL_DEP_SON_ADOP_INVALID:
                enumValue = BehaviorEnum.INCL_DEP_SON_DENTAL_ADOP_INVALID;
                break;
            case INCL_DEP_SON_MAJOR_WORK:
                enumValue = BehaviorEnum.INCL_DEP_SON_DENTAL_MAJOR_WORK;
                break;
            case INCL_DEP_SON_MAJOR:
                enumValue = BehaviorEnum.INCL_DEP_SON_DENTAL_MAJOR;
                break;
            case INCL_DEP_SON_INVALID:
                enumValue = BehaviorEnum.INCL_DEP_SON_DENTAL_INVALID;
                break;
            case INCL_DEP_SON_ADOP_MAJOR_STU:
                enumValue = BehaviorEnum.INCL_DEP_SON_DENTAL_ADOP_MAJOR_STU;
                break;
            case INCL_DEP_SON_MAJOR_STU:
                enumValue = BehaviorEnum.INCL_DEP_SON_DENTAL_MAJOR_STU;
                DependentBenefitAdhesionFragment.codeTerm = Constants.TERMS_OF_ACTIVATION_PLAN_ODONTO_MAJOR;
                break;
            case INCL_DEP_SON_ADOP_MAJOR_WORK_STU:
                enumValue = BehaviorEnum.INCL_DEP_SON_DENTAL_ADOP_MAJOR_WORK_STU;
                break;
            case INCL_DEP_SON_MAJOR_WORK_STU:
                enumValue = BehaviorEnum.INCL_DEP_SON_DENTAL_MAJOR_WORK_STU;
                break;
            case INCL_DEP_CUR_MAJOR_WORK:
                enumValue = BehaviorEnum.INCL_DEP_CUR_DENTAL_MAJOR_WORK;
                break;
            case INCL_DEP_CUR_MAJOR:
                enumValue = BehaviorEnum.INCL_DEP_CUR_DENTAL_MAJOR;
                break;
            case INCL_DEP_CUR_INVALID:
                enumValue = BehaviorEnum.INCL_DEP_CUR_DENTAL_INVALID;
                break;
            case INCL_DEP_CUR_MAJOR_STU:
                enumValue = BehaviorEnum.INCL_DEP_CUR_DENTAL_MAJOR_STU;
                break;
            case INCL_DEP_CUR_MAJOR_WORK_STU:
                enumValue = BehaviorEnum.INCL_DEP_CUR_DENTAL_MAJOR_WORK_STU;
                break;
            case INCL_DEP_GUARD_MAJOR_WORK:
                enumValue = BehaviorEnum.INCL_DEP_GUARD_DENTAL_MAJOR_WORK;
                break;
            case INCL_DEP_GUARD_MAJOR:
                enumValue = BehaviorEnum.INCL_DEP_GUARD_DENTAL_MAJOR;
                break;
            case INCL_DEP_GUARD_INVALID:
                enumValue = BehaviorEnum.INCL_DEP_GUARD_DENTAL_INVALID;
                break;
            case INCL_DEP_GUARD_MAJOR_STU:
                enumValue = BehaviorEnum.INCL_DEP_GUARD_DENTAL_MAJOR_STU;
                break;
            case INCL_DEP_GUARD_MAJOR_WORK_STU:
                enumValue = BehaviorEnum.INCL_DEP_GUARD_DENTAL_MAJOR_WORK_STU;
                break;
            case INCL_DEP_ENT_SHARED_MAJOR_WORK:
                enumValue = BehaviorEnum.INCL_DEP_ENT_DENTAL_SHARED_MAJOR_WORK;
                break;
            case INCL_DEP_ENT_SHARED_MAJOR:
                enumValue = BehaviorEnum.INCL_DEP_ENT_DENTAL_SHARED_MAJOR;
                break;
            case INCL_DEP_ENT_SHARED_INVALID:
                enumValue = BehaviorEnum.INCL_DEP_ENT_DENTAL_SHARED_INVALID;
                break;
            case INCL_DEP_ENT_NO_SHARED_MAJOR_WORK:
                enumValue = BehaviorEnum.INCL_DEP_ENT_DENTAL_NO_SHARED_MAJOR_WORK;
                break;
            case INCL_DEP_ENT_NO_SHARED_MAJOR:
                enumValue = BehaviorEnum.INCL_DEP_ENT_DENTAL_NO_SHARED_MAJOR;
                break;
            case INCL_DEP_ENT_NO_SHARED_INVALID:
                enumValue = BehaviorEnum.INCL_DEP_ENT_DENTAL_NO_SHARED_INVALID;
                break;
            case INCL_DEP_ENT_MAJOR_WORK:
                enumValue = BehaviorEnum.INCL_DEP_ENT_DENTAL_MAJOR_WORK;
                break;
            case INCL_DEP_ENT_MAJOR:
                enumValue = BehaviorEnum.INCL_DEP_ENT_DENTAL_MAJOR;
                break;
            case INCL_DEP_ENT_INVALID:
                enumValue = BehaviorEnum.INCL_DEP_ENT_DENTAL_INVALID;
                break;
            case INCL_DEP_ENT_MAJOR_STU:
                enumValue = BehaviorEnum.INCL_DEP_ENT_DENTAL_MAJOR_STU;
                break;
            case INCL_DEP_ENT_MAJOR_WORK_STU:
                enumValue = BehaviorEnum.INCL_DEP_ENT_DENTAL_MAJOR_WORK_STU;
                break;
            case INCL_DEP_ENT_SHARED_MAJOR_STU:
                enumValue = BehaviorEnum.INCL_DEP_ENT_DENTAL_SHARED_MAJOR_STU;
                break;
            case INCL_DEP_ENT_SHARED_MAJOR_WORK_STU:
                enumValue = BehaviorEnum.INCL_DEP_ENT_DENTAL_SHARED_MAJOR_WORK_STU;
                break;
            case INCL_DEP_ENT_NO_SHARED_MAJOR_STU:
                enumValue = BehaviorEnum.INCL_DEP_ENT_DENTAL_NO_SHARED_MAJOR_STU;
                break;
            case INCL_DEP_ENT_NO_SHARED_MAJOR_WORK_STU:
                enumValue = BehaviorEnum.INCL_DEP_ENT_DENTAL_NO_SHARED_MAJOR_WORK_STU;
                break;
            case INCL_DEP_DAD:
                enumValue = BehaviorEnum.INCL_DEP_DAD_DENTAL;
                break;
            case INCL_DEP_MOM:
                enumValue = BehaviorEnum.INCL_DEP_MOM_DENTAL;
                break;
            case INCL_DEP_TUT_MAJOR_WORK:
                enumValue = BehaviorEnum.INCL_DEP_TUT_DENTAL_MAJOR_WORK;
                break;
            case INCL_DEP_TUT_MAJOR:
                enumValue = BehaviorEnum.INCL_DEP_TUT_DENTAL_MAJOR;
                break;
            case INCL_DEP_TUT_INVALID:
                enumValue = BehaviorEnum.INCL_DEP_TUT_DENTAL_INVALID;
                break;
            case INCL_DEP_TUT_MAJOR_STU:
                enumValue = BehaviorEnum.INCL_DEP_TUT_DENTAL_MAJOR_STU;
                break;
            case INCL_DEP_TUT_MAJOR_WORK_STU:
                enumValue = BehaviorEnum.INCL_DEP_TUT_DENTAL_MAJOR_WORK_STU;
                break;
            case INCL_DEP_COMP:
                enumValue = BehaviorEnum.INCL_DEP_COMP_DENTAL;
                break;
            case INCL_DEP_SON_ADOP_MINOR:
                enumValue = BehaviorEnum.INCL_DEP_SON_DENTAL_ADOP_MINOR;
                break;
            case INCL_DEP_SON_MINOR:
                enumValue = BehaviorEnum.INCL_DEP_SON_DENTAL_MINOR;
                break;
            case INCL_DEP_CUR_MINOR:
                enumValue = BehaviorEnum.INCL_DEP_CUR_DENTAL_MINOR;
                break;
            case INCL_DEP_GUARD_MINOR:
                enumValue = BehaviorEnum.INCL_DEP_GUARD_DENTAL_MINOR;
                break;
            case INCL_DEP_ENT_SHARED_MINOR:
                enumValue = BehaviorEnum.INCL_DEP_ENT_DENTAL_SHARED_MINOR;
                break;
            case INCL_DEP_ENT_NO_SHARED_MINOR:
                enumValue = BehaviorEnum.INCL_DEP_ENT_DENTAL_NO_SHARED_MINOR;
                break;
            case INCL_DEP_ENT_MINOR:
                enumValue = BehaviorEnum.INCL_DEP_ENT_DENTAL_MINOR;
                break;
            case INCL_DEP_TUT_MINOR:
                enumValue = BehaviorEnum.INCL_DEP_TUT_DENTAL_MINOR;
                break;
            case INCL_DEP_COMP_HOLDER_MARRIGE:
                enumValue = BehaviorEnum.INCL_DEP_COMP_DENTAL_HOLDER_MARRIGE;
                break;
            case INCL_DEP_SON_ADOP_MAJOR_FAHZ:
                enumValue = BehaviorEnum.INCL_DEP_SON_DENTAL_ADOP_MAJOR_FAHZ;
                break;
            case INCL_DEP_SON_MAJOR_FAHZ:
                enumValue = BehaviorEnum.INCL_DEP_SON_DENTAL_MAJOR_FAHZ;
                break;
            case INCL_DEP_CUR_MAJOR_FAHZ:
                enumValue = BehaviorEnum.INCL_DEP_CUR_DENTAL_MAJOR_FAHZ;
                break;
            case INCL_DEP_GUARD_MAJOR_FAHZ:
                enumValue = BehaviorEnum.INCL_DEP_GUARD_DENTAL_MAJOR_FAHZ;
                break;
            case INCL_DEP_ENT_SHARED_MAJOR_FAHZ:
                enumValue = BehaviorEnum.INCL_DEP_ENT_DENTAL_SHARED_MAJOR_FAHZ;
                break;
            case INCL_DEP_ENT_NO_SHARED_MAJOR_FAHZ:
                enumValue = BehaviorEnum.INCL_DEP_ENT_DENTAL_NO_SHARED_MAJOR_FAHZ;
                break;
            case INCL_DEP_ENT_MAJOR_FAHZ:
                enumValue = BehaviorEnum.INCL_DEP_ENT_DENTAL_MAJOR_FAHZ;
                break;
            case INCL_DEP_TUT_MAJOR_FAHZ:
                enumValue = BehaviorEnum.INCL_DEP_TUT_DENTAL_MAJOR_FAHZ;
                break;
        }

        return enumValue;
    }

    public static BehaviorEnum verifyPharmacyBehavior(BehaviorEnum SystemBehavior) {

        BehaviorEnum enumValue = null;
        switch (SystemBehavior) {
            case INCL_DEP_CONJ:
                enumValue = BehaviorEnum.INCL_DEP_CONJ_PHARMACY;
                break;
            case INCL_DEP_COMP_BOTH_MARRIGE:
                enumValue = BehaviorEnum.INCL_DEP_COMP_PHARMACY_BOTH_MARRIGE;
                break;
            case INCL_DEP_COMP_DEP_MARRIGE:
                enumValue = BehaviorEnum.INCL_DEP_COMP_PHARMACY_DEP_MARRIGE;
                break;
            case INCL_DEP_SON_ADOP_MAJOR_WORK:
                enumValue = BehaviorEnum.INCL_DEP_SON_PHARMACY_ADOP_MAJOR_WORK;
                break;
            case INCL_DEP_SON_ADOP_MAJOR:
                enumValue = BehaviorEnum.INCL_DEP_SON_PHARMACY_ADOP_MAJOR;
                break;
            case INCL_DEP_SON_ADOP_INVALID:
                enumValue = BehaviorEnum.INCL_DEP_SON_PHARMACY_ADOP_INVALID;
                break;
            case INCL_DEP_SON_MAJOR_WORK:
                enumValue = BehaviorEnum.INCL_DEP_SON_PHARMACY_MAJOR_WORK;
                break;
            case INCL_DEP_SON_MAJOR:
                enumValue = BehaviorEnum.INCL_DEP_SON_PHARMACY_MAJOR;
                break;
            case INCL_DEP_SON_INVALID:
                enumValue = BehaviorEnum.INCL_DEP_SON_PHARMACY_INVALID;
                break;
            case INCL_DEP_SON_ADOP_MAJOR_STU:
                enumValue = BehaviorEnum.INCL_DEP_SON_PHARMACY_ADOP_MAJOR_STU;
                break;
            case INCL_DEP_SON_MAJOR_STU:
                enumValue = BehaviorEnum.INCL_DEP_SON_PHARMACY_MAJOR_STU;
                break;
            case INCL_DEP_SON_ADOP_MAJOR_WORK_STU:
                enumValue = BehaviorEnum.INCL_DEP_SON_PHARMACY_ADOP_MAJOR_WORK_STU;
                break;
            case INCL_DEP_SON_MAJOR_WORK_STU:
                enumValue = BehaviorEnum.INCL_DEP_SON_PHARMACY_MAJOR_WORK_STU;
                break;
            case INCL_DEP_CUR_MAJOR_WORK:
                enumValue = BehaviorEnum.INCL_DEP_CUR_PHARMACY_MAJOR_WORK;
                break;
            case INCL_DEP_CUR_MAJOR:
                enumValue = BehaviorEnum.INCL_DEP_CUR_PHARMACY_MAJOR;
                break;
            case INCL_DEP_CUR_INVALID:
                enumValue = BehaviorEnum.INCL_DEP_CUR_PHARMACY_INVALID;
                break;
            case INCL_DEP_CUR_MAJOR_STU:
                enumValue = BehaviorEnum.INCL_DEP_CUR_PHARMACY_MAJOR_STU;
                break;
            case INCL_DEP_CUR_MAJOR_WORK_STU:
                enumValue = BehaviorEnum.INCL_DEP_CUR_PHARMACY_MAJOR_WORK_STU;
                break;
            case INCL_DEP_GUARD_MAJOR_WORK:
                enumValue = BehaviorEnum.INCL_DEP_GUARD_PHARMACY_MAJOR_WORK;
                break;
            case INCL_DEP_GUARD_MAJOR:
                enumValue = BehaviorEnum.INCL_DEP_GUARD_PHARMACY_MAJOR;
                break;
            case INCL_DEP_GUARD_INVALID:
                enumValue = BehaviorEnum.INCL_DEP_GUARD_PHARMACY_INVALID;
                break;
            case INCL_DEP_GUARD_MAJOR_STU:
                enumValue = BehaviorEnum.INCL_DEP_GUARD_PHARMACY_MAJOR_STU;
                break;
            case INCL_DEP_GUARD_MAJOR_WORK_STU:
                enumValue = BehaviorEnum.INCL_DEP_GUARD_PHARMACY_MAJOR_WORK_STU;
                break;
            case INCL_DEP_ENT_SHARED_MAJOR_WORK:
                enumValue = BehaviorEnum.INCL_DEP_ENT_PHARMACY_SHARED_MAJOR_WORK;
                break;
            case INCL_DEP_ENT_SHARED_MAJOR:
                enumValue = BehaviorEnum.INCL_DEP_ENT_PHARMACY_SHARED_MAJOR;
                break;
            case INCL_DEP_ENT_SHARED_INVALID:
                enumValue = BehaviorEnum.INCL_DEP_ENT_PHARMACY_SHARED_INVALID;
                break;
            case INCL_DEP_ENT_NO_SHARED_MAJOR_WORK:
                enumValue = BehaviorEnum.INCL_DEP_ENT_PHARMACY_NO_SHARED_MAJOR_WORK;
                break;
            case INCL_DEP_ENT_NO_SHARED_MAJOR:
                enumValue = BehaviorEnum.INCL_DEP_ENT_PHARMACY_NO_SHARED_MAJOR;
                break;
            case INCL_DEP_ENT_NO_SHARED_INVALID:
                enumValue = BehaviorEnum.INCL_DEP_ENT_PHARMACY_NO_SHARED_INVALID;
                break;
            case INCL_DEP_ENT_MAJOR_WORK:
                enumValue = BehaviorEnum.INCL_DEP_ENT_PHARMACY_MAJOR_WORK;
                break;
            case INCL_DEP_ENT_MAJOR:
                enumValue = BehaviorEnum.INCL_DEP_ENT_PHARMACY_MAJOR;
                break;
            case INCL_DEP_ENT_INVALID:
                enumValue = BehaviorEnum.INCL_DEP_ENT_PHARMACY_INVALID;
                break;
            case INCL_DEP_ENT_MAJOR_STU:
                enumValue = BehaviorEnum.INCL_DEP_ENT_PHARMACY_MAJOR_STU;
                break;
            case INCL_DEP_ENT_MAJOR_WORK_STU:
                enumValue = BehaviorEnum.INCL_DEP_ENT_PHARMACY_MAJOR_WORK_STU;
                break;
            case INCL_DEP_ENT_SHARED_MAJOR_STU:
                enumValue = BehaviorEnum.INCL_DEP_ENT_PHARMACY_SHARED_MAJOR_STU;
                break;
            case INCL_DEP_ENT_SHARED_MAJOR_WORK_STU:
                enumValue = BehaviorEnum.INCL_DEP_ENT_PHARMACY_SHARED_MAJOR_WORK_STU;
                break;
            case INCL_DEP_ENT_NO_SHARED_MAJOR_STU:
                enumValue = BehaviorEnum.INCL_DEP_ENT_PHARMACY_NO_SHARED_MAJOR_STU;
                break;
            case INCL_DEP_ENT_NO_SHARED_MAJOR_WORK_STU:
                enumValue = BehaviorEnum.INCL_DEP_ENT_PHARMACY_NO_SHARED_MAJOR_WORK_STU;
                break;
            case INCL_DEP_DAD:
                enumValue = BehaviorEnum.INCL_DEP_DAD_PHARMACY;
                break;
            case INCL_DEP_MOM:
                enumValue = BehaviorEnum.INCL_DEP_MOM_PHARMACY;
                break;
            case INCL_DEP_TUT_MAJOR_WORK:
                enumValue = BehaviorEnum.INCL_DEP_TUT_PHARMACY_MAJOR_WORK;
                break;
            case INCL_DEP_TUT_MAJOR:
                enumValue = BehaviorEnum.INCL_DEP_TUT_PHARMACY_MAJOR;
                break;
            case INCL_DEP_TUT_INVALID:
                enumValue = BehaviorEnum.INCL_DEP_TUT_PHARMACY_INVALID;
                break;
            case INCL_DEP_TUT_MAJOR_STU:
                enumValue = BehaviorEnum.INCL_DEP_TUT_PHARMACY_MAJOR_STU;
                break;
            case INCL_DEP_TUT_MAJOR_WORK_STU:
                enumValue = BehaviorEnum.INCL_DEP_TUT_PHARMACY_MAJOR_WORK_STU;
                break;
            case INCL_DEP_COMP:
                enumValue = BehaviorEnum.INCL_DEP_COMP_PHARMACY;
                break;
            case INCL_DEP_SON_ADOP_MINOR:
                enumValue = BehaviorEnum.INCL_DEP_SON_PHARMACY_ADOP_MINOR;
                break;
            case INCL_DEP_SON_MINOR:
                enumValue = BehaviorEnum.INCL_DEP_SON_PHARMACY_MINOR;
                break;
            case INCL_DEP_CUR_MINOR:
                enumValue = BehaviorEnum.INCL_DEP_CUR_PHARMACY_MINOR;
                break;
            case INCL_DEP_GUARD_MINOR:
                enumValue = BehaviorEnum.INCL_DEP_GUARD_PHARMACY_MINOR;
                break;
            case INCL_DEP_ENT_SHARED_MINOR:
                enumValue = BehaviorEnum.INCL_DEP_ENT_PHARMACY_SHARED_MINOR;
                break;
            case INCL_DEP_ENT_NO_SHARED_MINOR:
                enumValue = BehaviorEnum.INCL_DEP_ENT_PHARMACY_NO_SHARED_MINOR;
                break;
            case INCL_DEP_ENT_MINOR:
                enumValue = BehaviorEnum.INCL_DEP_ENT_PHARMACY_MINOR;
                break;
            case INCL_DEP_TUT_MINOR:
                enumValue = BehaviorEnum.INCL_DEP_TUT_PHARMACY_MINOR;
                break;
            case INCL_DEP_COMP_HOLDER_MARRIGE:
                enumValue = BehaviorEnum.INCL_DEP_COMP_PHARMACY_HOLDER_MARRIGE;
                break;
            case INCL_DEP_SON_ADOP_MAJOR_FAHZ:
                enumValue = BehaviorEnum.INCL_DEP_SON_PHARMACY_ADOP_MAJOR_FAHZ;
                break;
            case INCL_DEP_SON_MAJOR_FAHZ:
                enumValue = BehaviorEnum.INCL_DEP_SON_PHARMACY_MAJOR_FAHZ;
                break;
            case INCL_DEP_CUR_MAJOR_FAHZ:
                enumValue = BehaviorEnum.INCL_DEP_CUR_PHARMACY_MAJOR_FAHZ;
                break;
            case INCL_DEP_GUARD_MAJOR_FAHZ:
                enumValue = BehaviorEnum.INCL_DEP_GUARD_PHARMACY_MAJOR_FAHZ;
                break;
            case INCL_DEP_ENT_SHARED_MAJOR_FAHZ:
                enumValue = BehaviorEnum.INCL_DEP_ENT_PHARMACY_SHARED_MAJOR_FAHZ;
                break;
            case INCL_DEP_ENT_NO_SHARED_MAJOR_FAHZ:
                enumValue = BehaviorEnum.INCL_DEP_ENT_PHARMACY_NO_SHARED_MAJOR_FAHZ;
                break;
            case INCL_DEP_ENT_MAJOR_FAHZ:
                enumValue = BehaviorEnum.INCL_DEP_ENT_PHARMACY_MAJOR_FAHZ;
                break;
            case INCL_DEP_TUT_MAJOR_FAHZ:
                enumValue = BehaviorEnum.INCL_DEP_TUT_PHARMACY_MAJOR_FAHZ;
                break;
        }

        return enumValue;
    }

    public static boolean validateMajorNotWorking(int behaviorID) {
        boolean notWorking = false;

        if(behaviorID == BehaviorEnum.INCL_DEP_SON_ADOP_MAJOR.id() ||
                behaviorID == BehaviorEnum.INCL_DEP_SON_MAJOR.id() ||
                behaviorID == BehaviorEnum.INCL_DEP_CUR_MAJOR.id() ||
                behaviorID == BehaviorEnum.INCL_DEP_GUARD_MAJOR.id() ||
                behaviorID == BehaviorEnum.INCL_DEP_ENT_SHARED_MAJOR.id() ||
                behaviorID == BehaviorEnum.INCL_DEP_ENT_NO_SHARED_MAJOR.id() ||
                behaviorID == BehaviorEnum.INCL_DEP_ENT_MAJOR.id() ||
                behaviorID == BehaviorEnum.INCL_DEP_TUT_MAJOR.id() ||
                behaviorID == BehaviorEnum.INCL_DEP_SON_ADOP_MAJOR_STU.id() ||
                behaviorID == BehaviorEnum.INCL_DEP_SON_MAJOR_STU.id() ||
                behaviorID == BehaviorEnum.INCL_DEP_CUR_MAJOR_STU.id() ||
                behaviorID == BehaviorEnum.INCL_DEP_GUARD_MAJOR_STU.id() ||
                behaviorID == BehaviorEnum.INCL_DEP_ENT_MAJOR_STU.id() ||
                behaviorID == BehaviorEnum.INCL_DEP_ENT_SHARED_MAJOR_STU.id() ||
                behaviorID == BehaviorEnum.INCL_DEP_ENT_NO_SHARED_MAJOR_STU.id() ||
                behaviorID == BehaviorEnum.INCL_DEP_TUT_MAJOR_STU.id())
            notWorking =true;

        return notWorking;
    }

    public static boolean validateShared(int behaviorID) {
        boolean shared = false;

        if(behaviorID == BehaviorEnum. INCL_DEP_ENT_SHARED_MAJOR_WORK.id() ||
                behaviorID == BehaviorEnum.INCL_DEP_ENT_SHARED_MAJOR.id() ||
                 behaviorID == BehaviorEnum.INCL_DEP_ENT_SHARED_INVALID.id() ||
                behaviorID == BehaviorEnum.INCL_DEP_ENT_SHARED_MINOR.id() ||
                behaviorID == BehaviorEnum.INCL_DEP_ENT_SHARED_MAJOR_STU.id()||
        behaviorID == BehaviorEnum.INCL_DEP_ENT_SHARED_MAJOR_WORK_STU.id() ||
                behaviorID == BehaviorEnum.INCL_DEP_ENT_SHARED_MAJOR_FAHZ.id())
            shared =true;

        return shared;
    }

        public static boolean validateMinor(int behaviorID) {
            boolean minor = false;

            if(behaviorID == BehaviorEnum. INCL_DEP_SON_ADOP_MINOR.id() ||
                    behaviorID == BehaviorEnum.INCL_DEP_SON_MINOR.id() ||
                    behaviorID == BehaviorEnum.INCL_DEP_CUR_MINOR.id() ||
                    behaviorID == BehaviorEnum.INCL_DEP_GUARD_MINOR.id() ||
                    behaviorID == BehaviorEnum.INCL_DEP_ENT_SHARED_MINOR.id()||
                    behaviorID == BehaviorEnum.INCL_DEP_ENT_NO_SHARED_MINOR.id() ||
                    behaviorID == BehaviorEnum.INCL_DEP_ENT_MINOR.id() ||
                    behaviorID == BehaviorEnum.INCL_DEP_TUT_MINOR.id())
                minor =true;

            return minor;
        }

}
